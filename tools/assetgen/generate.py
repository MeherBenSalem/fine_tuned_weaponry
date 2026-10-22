#!/usr/bin/env python3
"""Generates Runeborn assets and per-version data from the core catalog.

Usage: python3 tools/assetgen/generate.py

1. Compiles core and runs io.nightbeam.runeborn.core.tools.Export to get catalog.json.
2. Writes version-independent assets to shared/src/main/resources/assets/runeborn.
3. Writes per-version data (recipes, loot tables, tags, item definitions) to
   versions/<mc>/common/src/generated/resources.

Re-run after changing weapons, materials, bosses, recipes or English strings in core. Output is
committed. Everything is generated into tools/assetgen/build/stage first and then synced, so files
that did not change are left untouched (no needless rebuilds) and stale files are removed.
"""
import glob
import json
import os
import shutil
import subprocess
import sys

from PIL import Image, ImageDraw, ImageFilter, ImageFont

sys.path.insert(0, os.path.dirname(__file__))
import materials  # noqa: E402
import sprites  # noqa: E402

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
HERE = os.path.dirname(os.path.abspath(__file__))
BUILD = os.path.join(HERE, 'build')
STAGE = os.path.join(BUILD, 'stage')
SHARED_REL = os.path.join('shared', 'src', 'main', 'resources')
ASSETS_REL = os.path.join(SHARED_REL, 'assets', 'runeborn')
ASSETS = os.path.join(STAGE, ASSETS_REL)
# Generated subtrees of ASSETS_REL; anything else under assets/runeborn is hand-written.
ASSET_DIRS = ('textures', 'models', 'blockstates', 'lang', 'font')
LEGACY = os.path.join(HERE, 'legacy')
NS = 'runeborn'

VERSIONS = {
    # plural: data folder names (recipes/ vs recipe/); recipe: ingredient/result syntax;
    # enchant: enchantable item tags; items_dir: assets/<ns>/items definitions;
    # advancement: icon and background syntax.
    '1.20.1': {'plural': True, 'recipe': 'legacy', 'enchant': None, 'items_dir': False, 'advancement': 'legacy'},
    '1.21.1': {'plural': False, 'recipe': 'object', 'enchant': ['sword', 'sharp_weapon', 'fire_aspect'],
               'items_dir': False, 'advancement': 'object'},
    '26.2': {'plural': False, 'recipe': 'string', 'enchant': ['melee_weapon', 'sharp_weapon', 'fire_aspect'],
             'items_dir': True, 'advancement': 'asset'},
    '26.3': {'plural': False, 'recipe': 'string', 'enchant': ['melee_weapon', 'sharp_weapon', 'fire_aspect'],
             'items_dir': True, 'advancement': 'asset'},
}
FEAT_BACKGROUND = 'block/polished_blackstone_bricks'


def write_json(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
        f.write('\n')


def find_jdk():
    for cand in [os.environ.get('JAVA_HOME', ''), '/opt/jdks/jdk-25.0.5+8', '/usr/lib/jvm/java-21-openjdk-amd64']:
        if cand and os.path.exists(os.path.join(cand, 'bin', 'javac')):
            return cand
    return None


def find_gson():
    hits = glob.glob(os.path.expanduser('~/.gradle/caches/modules-2/files-2.1/com.google.code.gson/gson/*/*/gson-*.jar'))
    hits = [h for h in hits if 'sources' not in h]
    if not hits:
        sys.exit('gson jar not found in the Gradle cache; build any workspace once first')
    return sorted(hits)[-1]


def export_catalog():
    jdk = find_jdk()
    if not jdk:
        sys.exit('No JDK found (set JAVA_HOME)')
    gson = find_gson()
    classes = os.path.join(BUILD, 'classes')
    shutil.rmtree(classes, ignore_errors=True)
    os.makedirs(classes)
    sources = glob.glob(os.path.join(ROOT, 'core', 'src', 'main', 'java', '**', '*.java'), recursive=True)
    subprocess.check_call([os.path.join(jdk, 'bin', 'javac'), '-encoding', 'UTF-8', '--release', '17', '-nowarn',
                           '-cp', gson, '-d', classes] + sources)
    out = os.path.join(BUILD, 'catalog.json')
    subprocess.check_call([os.path.join(jdk, 'bin', 'java'), '-cp', classes + os.pathsep + gson,
                           'io.nightbeam.runeborn.core.tools.Export', out])
    with open(out, encoding='utf-8') as f:
        return json.load(f)


# ------------------------------------------------------------------------------------------------ shared assets

def gen_textures(catalog):
    item_dir = os.path.join(ASSETS, 'textures', 'item')
    os.makedirs(item_dir, exist_ok=True)
    for w in catalog['weapons']:
        kind, _, rest = w['model'].partition(':')
        if kind == 'sprite':
            template, palette = rest.split(':')
            sprites.render(template, palette).save(os.path.join(item_dir, w['id'] + '.png'))
    for m in catalog['materials']:
        materials.render(m['texture'], catalog).save(os.path.join(item_dir, m['id'] + '.png'))


def gen_legacy(catalog):
    """Copies the hand-built 3D weapon models from the original mod and their textures."""
    tex_dir = os.path.join(ASSETS, 'textures', 'item', 'legacy')
    model_dir = os.path.join(ASSETS, 'models', 'custom')
    os.makedirs(tex_dir, exist_ok=True)
    os.makedirs(model_dir, exist_ok=True)
    for w in catalog['weapons']:
        kind, _, rest = w['model'].partition(':')
        if kind != 'custom':
            continue
        model, textures = rest.split(':', 1)
        with open(os.path.join(LEGACY, 'models', model + '.json'), encoding='utf-8') as f:
            data = json.load(f)
        data.pop('parent', None)
        data.pop('credit', None)
        write_json(os.path.join(model_dir, model + '.json'), data)
        for pair in textures.split(','):
            _, tex = pair.split('=')
            shutil.copy(os.path.join(LEGACY, 'textures', tex + '.png'), os.path.join(tex_dir, tex + '.png'))


def gen_models(catalog):
    model_dir = os.path.join(ASSETS, 'models', 'item')
    for w in catalog['weapons']:
        kind, _, rest = w['model'].partition(':')
        if kind == 'sprite':
            data = {'parent': 'minecraft:item/handheld', 'textures': {'layer0': f'{NS}:item/{w["id"]}'}}
        else:
            model, textures = rest.split(':', 1)
            tex = {}
            for pair in textures.split(','):
                key, name = pair.split('=')
                tex[key] = f'{NS}:item/legacy/{name}'
                tex['particle'] = f'{NS}:item/legacy/{name}'
            data = {'parent': f'{NS}:custom/{model}', 'textures': tex}
        write_json(os.path.join(model_dir, w['id'] + '.json'), data)
    for m in catalog['materials']:
        write_json(os.path.join(model_dir, m['id'] + '.json'),
                   {'parent': 'minecraft:item/generated', 'textures': {'layer0': f'{NS}:item/{m["id"]}'}})
    write_json(os.path.join(model_dir, 'ember_shrine.json'), {'parent': f'{NS}:block/ember_shrine'})


def gen_shrine():
    tex = os.path.join(ASSETS, 'textures', 'block')
    os.makedirs(tex, exist_ok=True)
    for name, img in shrine_textures().items():
        img.save(os.path.join(tex, name + '.png'))
    s = f'{NS}:block/shrine_stone'
    p = f'{NS}:block/shrine_pillar'
    g = f'{NS}:block/shrine_gold'
    e = f'{NS}:block/shrine_ember'

    def faces(side, top, bottom, uv=None):
        out = {}
        for d in ('north', 'south', 'east', 'west'):
            out[d] = {'texture': '#' + side}
            if uv:
                out[d]['uv'] = uv
        out['up'] = {'texture': '#' + top}
        out['down'] = {'texture': '#' + bottom}
        return out

    model = {
        'parent': 'minecraft:block/block',
        'textures': {'stone': s, 'pillar': p, 'gold': g, 'ember': e, 'particle': s},
        'elements': [
            {'from': [1, 0, 1], 'to': [15, 3, 15], 'faces': faces('stone', 'stone', 'stone', [1, 13, 15, 16])},
            {'from': [3, 3, 3], 'to': [13, 10, 13], 'faces': faces('pillar', 'stone', 'stone', [3, 6, 13, 13])},
            {'from': [2, 10, 2], 'to': [14, 13, 14], 'faces': faces('gold', 'ember', 'gold', [2, 3, 14, 6])},
            {'from': [1, 12, 1], 'to': [3, 15, 3], 'faces': faces('gold', 'gold', 'gold', [0, 0, 2, 3])},
            {'from': [13, 12, 13], 'to': [15, 15, 15], 'faces': faces('gold', 'gold', 'gold', [0, 0, 2, 3])},
            {'from': [1, 12, 13], 'to': [3, 15, 15], 'faces': faces('gold', 'gold', 'gold', [0, 0, 2, 3])},
            {'from': [13, 12, 1], 'to': [15, 15, 3], 'faces': faces('gold', 'gold', 'gold', [0, 0, 2, 3])},
        ],
    }
    write_json(os.path.join(ASSETS, 'models', 'block', 'ember_shrine.json'), model)
    write_json(os.path.join(ASSETS, 'blockstates', 'ember_shrine.json'),
               {'variants': {'': {'model': f'{NS}:block/ember_shrine'}}})


def shrine_textures():
    import random
    rnd = random.Random(7)

    def img():
        return Image.new('RGBA', (16, 16), (0, 0, 0, 255))

    stone = img()
    px = stone.load()
    for y in range(16):
        for x in range(16):
            row = y // 4
            offset = 4 if row % 2 else 0
            mortar = y % 4 == 3 or (x + offset) % 8 == 7
            base = 46 if mortar else 74 + rnd.randint(-8, 8)
            tint = (base, base - 2, base + 4)
            if not mortar and (y % 4 == 0):
                tint = tuple(min(255, c + 12) for c in tint)
            px[x, y] = tint + (255,)

    pillar = img()
    px = pillar.load()
    for y in range(16):
        for x in range(16):
            v = 52 + rnd.randint(-6, 6)
            px[x, y] = (v, v - 3, v + 2, 255)
    rune = ["................",
            ".......GG.......",
            "......G..G......",
            ".....G.GG.G.....",
            "....G.G..G.G....",
            ".......GG.......",
            ".......GG.......",
            "....GGGGGGGG....",
            ".......GG.......",
            "......G..G......",
            ".....G....G.....",
            ".......GG.......",
            "................",
            "................",
            "................",
            "................"]
    for y, row in enumerate(rune):
        for x, ch in enumerate(row):
            if ch == 'G':
                px[x, y] = (232, 194, 102, 255)
    for x in range(16):
        px[x, 15] = (232, 194, 102, 255)
        px[x, 0] = (150, 118, 52, 255)

    gold = img()
    px = gold.load()
    for y in range(16):
        for x in range(16):
            band = (y % 3 == 1)
            r, g, b = (232, 194, 102) if band else (176, 136, 58)
            if (x + y) % 5 == 0:
                r, g, b = 255, 236, 170
            px[x, y] = (r, g, b, 255)

    ember = img()
    px = ember.load()
    for y in range(16):
        for x in range(16):
            n = rnd.random()
            if n < 0.12:
                c = (255, 236, 160)
            elif n < 0.45:
                c = (255, 140, 40)
            elif n < 0.8:
                c = (200, 70, 20)
            else:
                c = (90, 30, 12)
            px[x, y] = c + (255,)
    return {'shrine_stone': stone, 'shrine_pillar': pillar, 'shrine_gold': gold, 'shrine_ember': ember}


def gen_lang(catalog):
    write_json(os.path.join(ASSETS, 'lang', 'en_us.json'), dict(sorted(catalog['lang'].items())))


def gen_font():
    font_dir = os.path.join(ASSETS, 'font')
    os.makedirs(font_dir, exist_ok=True)
    src = os.path.join(HERE, 'fonts', 'cinzel_bold.ttf')
    shutil.copy(src, os.path.join(font_dir, 'cinzel_bold.ttf'))
    write_json(os.path.join(font_dir, 'title.json'), {
        'providers': [
            {'type': 'ttf', 'file': f'{NS}:cinzel_bold.ttf', 'shift': [0, 1], 'size': 9.0, 'oversample': 4.0},
            {'type': 'reference', 'id': 'minecraft:default'},
        ]
    })


def gen_icon(catalog):
    size = 512
    img = Image.new('RGBA', (size, size), (10, 9, 8, 255))
    d = ImageDraw.Draw(img)
    for r in range(size // 2, 0, -2):
        t = r / (size / 2)
        c = (int(46 * (1 - t) + 10 * t), int(30 * (1 - t) + 9 * t), int(14 * (1 - t) + 8 * t), 255)
        d.ellipse([size / 2 - r, size / 2 - r - 30, size / 2 + r, size / 2 + r - 30], fill=c)
    gold = (227, 201, 138, 255)
    d.ellipse([86, 56, 426, 396], outline=gold, width=6)
    d.ellipse([110, 80, 402, 372], outline=(156, 133, 88, 255), width=2)
    for i in range(24):
        import math
        a = math.pi * 2 * i / 24
        x1, y1 = 256 + math.cos(a) * 160, 226 + math.sin(a) * 160
        x2, y2 = 256 + math.cos(a) * 172, 226 + math.sin(a) * 172
        d.line([x1, y1, x2, y2], fill=gold, width=3)
    sword = sprites.render('colossal', 'crown')
    sword = sword.resize((256, 256), Image.NEAREST)
    glow = sword.filter(ImageFilter.GaussianBlur(10))
    img.alpha_composite(glow, (128, 98))
    img.alpha_composite(sword, (128, 98))
    font = ImageFont.truetype(os.path.join(HERE, 'fonts', 'cinzel_bold.ttf'), 70)
    text = 'RUNEBORN'
    w = d.textlength(text, font=font)
    d.text(((size - w) / 2 + 3, 413), text, font=font, fill=(0, 0, 0, 200))
    d.text(((size - w) / 2, 410), text, font=font, fill=gold)
    img = img.resize((256, 256), Image.LANCZOS)
    img.save(os.path.join(ASSETS, 'icon.png'))


# ------------------------------------------------------------------------------------------------ per-version data

def ingredient(item, style):
    if style == 'string':
        return item
    if item.startswith('#'):
        return {'tag': item[1:]}
    return {'item': item}


def result(item, count, style):
    if style == 'legacy':
        return {'item': item, 'count': count}
    return {'id': item, 'count': count}


def shaped(pattern, key, out, count, style):
    return {'type': 'minecraft:crafting_shaped', 'category': 'misc', 'pattern': pattern,
            'key': {k: ingredient(v, style) for k, v in key.items()}, 'result': result(out, count, style)}


def shapeless(items, out, count, style):
    return {'type': 'minecraft:crafting_shapeless', 'category': 'misc',
            'ingredients': [ingredient(i, style) for i in items], 'result': result(out, count, style)}


def advancement(feat, style):
    """One feat as an advancement with a single impossible criterion; the mod grants it."""
    icon = {'item': feat['icon']} if style == 'legacy' else {'id': feat['icon']}
    display = {
        'icon': icon,
        'title': {'translate': feat['title']},
        'description': {'translate': feat['description']},
        'frame': feat['frame'],
        'show_toast': True,
        'announce_to_chat': feat['frame'] != 'task',
        'hidden': feat['hidden'],
    }
    if 'parent' not in feat:
        display['background'] = (f'minecraft:{FEAT_BACKGROUND}' if style == 'asset'
                                 else f'minecraft:textures/{FEAT_BACKGROUND}.png')
        display['show_toast'] = False
        display['announce_to_chat'] = False
    adv = {'display': display, 'criteria': {'granted': {'trigger': 'minecraft:impossible'}}}
    if 'parent' in feat:
        adv['parent'] = f"{NS}:{feat['parent']}"
    return adv


def gen_version(mc, cfg, catalog):
    base = os.path.join(STAGE, version_generated_rel(mc))
    data = os.path.join(base, 'data')
    style = cfg['recipe']
    recipes = os.path.join(data, NS, 'recipes' if cfg['plural'] else 'recipe')
    loot = os.path.join(data, NS, 'loot_tables' if cfg['plural'] else 'loot_table')
    tag_items = 'items' if cfg['plural'] else 'item'
    tag_blocks = 'blocks' if cfg['plural'] else 'block'

    write_json(os.path.join(loot, 'blocks', 'ember_shrine.json'), {
        'type': 'minecraft:block',
        'pools': [{'rolls': 1, 'entries': [{'type': 'minecraft:item', 'name': f'{NS}:ember_shrine'}],
                   'conditions': [{'condition': 'minecraft:survives_explosion'}]}],
    })
    write_json(os.path.join(data, 'minecraft', 'tags', tag_blocks, 'mineable', 'pickaxe.json'),
               {'replace': False, 'values': [f'{NS}:ember_shrine']})

    for r in catalog['recipes']:
        if 'pattern' in r:
            recipe = shaped(r['pattern'], r['key'], r['result'], r['count'], style)
        else:
            recipe = shapeless(r['ingredients'], r['result'], r['count'], style)
        write_json(os.path.join(recipes, r['id'] + '.json'), recipe)

    advancements = os.path.join(data, NS, 'advancements' if cfg['plural'] else 'advancement')
    for feat in catalog['feats']:
        write_json(os.path.join(advancements, feat['path'] + '.json'), advancement(feat, cfg['advancement']))

    if cfg['enchant']:
        weapons = [f'{NS}:{w["id"]}' for w in catalog['weapons'] if not w['catalyst']]
        for tag in cfg['enchant']:
            write_json(os.path.join(data, 'minecraft', 'tags', tag_items, 'enchantable', tag + '.json'),
                       {'replace': False, 'values': weapons})

    if cfg['items_dir']:
        items = os.path.join(base, 'assets', NS, 'items')
        ids = [w['id'] for w in catalog['weapons']] + [m['id'] for m in catalog['materials']] + ['ember_shrine']
        for i in ids:
            write_json(os.path.join(items, i + '.json'), {'model': {'type': 'minecraft:model', 'model': f'{NS}:item/{i}'}})


def version_generated_rel(mc):
    return os.path.join('versions', mc, 'common', 'src', 'generated', 'resources')


def sync_file(src, dst):
    """Copies src over dst only if the bytes differ. Returns True if dst changed."""
    with open(src, 'rb') as f:
        data = f.read()
    if os.path.isfile(dst):
        with open(dst, 'rb') as f:
            if f.read() == data:
                return False
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    tmp = dst + '.tmp'
    with open(tmp, 'wb') as f:
        f.write(data)
    os.replace(tmp, dst)
    return True


def sync_tree(src, dst):
    """Makes dst match src. Returns (changed, removed) file counts."""
    changed = removed = 0
    keep = set()
    for dirpath, _, files in os.walk(src):
        for name in files:
            rel = os.path.relpath(os.path.join(dirpath, name), src)
            keep.add(rel)
            changed += sync_file(os.path.join(src, rel), os.path.join(dst, rel))
    for dirpath, _, files in os.walk(dst, topdown=False):
        for name in files:
            rel = os.path.relpath(os.path.join(dirpath, name), dst)
            if rel not in keep:
                os.remove(os.path.join(dst, rel))
                removed += 1
        if dirpath != dst and not os.listdir(dirpath):
            os.rmdir(dirpath)
    return changed, removed


def main():
    catalog = export_catalog()
    shutil.rmtree(STAGE, ignore_errors=True)
    gen_textures(catalog)
    gen_legacy(catalog)
    gen_models(catalog)
    gen_shrine()
    gen_lang(catalog)
    gen_font()
    gen_icon(catalog)
    for mc, cfg in VERSIONS.items():
        gen_version(mc, cfg, catalog)

    changed = removed = 0
    trees = [os.path.join(ASSETS_REL, sub) for sub in ASSET_DIRS] + [version_generated_rel(mc) for mc in VERSIONS]
    for rel in trees:
        c, r = sync_tree(os.path.join(STAGE, rel), os.path.join(ROOT, rel))
        changed, removed = changed + c, removed + r
    changed += sync_file(os.path.join(ASSETS, 'icon.png'), os.path.join(ROOT, ASSETS_REL, 'icon.png'))
    print(f'Synced: {changed} files written, {removed} removed')
    print(f'Generated assets for {len(catalog["weapons"])} weapons and {len(catalog["materials"])} materials')


if __name__ == '__main__':
    main()
