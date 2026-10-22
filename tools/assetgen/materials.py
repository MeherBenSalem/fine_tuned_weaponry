"""Sprites for non-weapon items, coloured per tier, affinity or boss."""
from PIL import Image

from sprites import SIZE, hexrgb, shade

M_TEMPLATES = {
    'stone': [
        "................",
        "................",
        "................",
        ".......OOO......",
        ".....OOHBbO.....",
        "....OHBBbbdO....",
        "...OHBBEbbddO...",
        "...OBBEEebddO...",
        "..OBBbEebbdddO..",
        "..ObbbbebbdddO..",
        "..OdbbbbbdddsO..",
        "...OddbbdddssO..",
        "....OOddddsOO...",
        "......OOOOO.....",
        "................",
        "................",
    ],
    'gem': [
        "................",
        "................",
        "......OOOO......",
        ".....OHBBbO.....",
        "....OHBBBbbO....",
        "...OHHBBbbbdO...",
        "...OOOOOOOOOO...",
        "...OBBbEbbbdO...",
        "....OBbEebdO....",
        ".....OBEedO.....",
        "......ObedO.....",
        ".......OdO......",
        "........O.......",
        "................",
        "................",
        "................",
    ],
    'whetstone': [
        "................",
        "................",
        "................",
        "................",
        "..........OOO...",
        "........OOHBbO..",
        "......OOHBEEbdO.",
        "....OOHBEEbbdO..",
        "..OOHBEEbbddO...",
        ".OHBEEbbddOO....",
        "ObBEbbddOO......",
        "OdbbddOO........",
        ".OddOO..........",
        "..OO............",
        "................",
        "................",
    ],
    'rune': [
        "................",
        ".......OO.......",
        "......OHHO......",
        ".....OHBBdO.....",
        "....OHBEEbdO....",
        "...OHBEHEEbdO...",
        "..OHBEEHEEebdO..",
        "..OBbEEEEeebdO..",
        "...OBbEEeebdO...",
        "....ObbEebdO....",
        ".....ObbbdO.....",
        "......OddO......",
        ".......OO.......",
        "................",
        "................",
        "................",
    ],
    'flask': [
        "................",
        "......OOOO......",
        "......OGGO......",
        "......OggO......",
        ".....OOBbOO.....",
        "....OBHbbbdO....",
        "...OBHbbbbbdO...",
        "...OHEEEEEEdO...",
        "..OBEHEEEEeedO..",
        "..OBEEEEEEeedO..",
        "..OBEEEEEeeedO..",
        "..ObEEEEeeeedO..",
        "...ObeeeeeedO...",
        "....OddddddO....",
        ".....OOOOOO.....",
        "................",
    ],
    'seed': [
        "................",
        "................",
        "........O.......",
        ".......OGO......",
        "......OOgOO.....",
        ".....OHBBbbO....",
        "....OHBBEbbdO...",
        "....OBBEEebdO...",
        "....OBbEeebdO...",
        "....ObbbebddO...",
        ".....ObbbddO....",
        "......OdddO.....",
        ".......OOO......",
        "................",
        "................",
        "................",
    ],
    'tear': [
        "................",
        ".......OO.......",
        "......OHBO......",
        "......OHbO......",
        ".....OHBbdO.....",
        ".....OHbbdO.....",
        "....OHBEbbdO....",
        "...OHBEEebbdO...",
        "...OBEEHEebdO...",
        "...OBEEEeebdO...",
        "...ObEEeeebdO...",
        "....ObeeebdO....",
        ".....OdddOO.....",
        "......OOO.......",
        "................",
        "................",
    ],
    'fragment': [
        "................",
        "................",
        "...O.....O......",
        "..OHO...OHO.....",
        "..OBBO.OHBO.....",
        "..OBbbOBbbO..O..",
        "..OBbbbbbbdOOHO.",
        "..OBEbbbEbbbBbO.",
        "..OGGGGGGGGGgdO.",
        "..OGgEgggEggggO.",
        "..OOOOOOOOOOOOO.",
        "................",
        "................",
        "................",
        "................",
        "................",
    ],
    'sigil': [
        "................",
        ".....OOOOOO.....",
        "....OGGGGGgO....",
        "...OGgOOOOggO...",
        "..OGgOEEEEOggO..",
        "..OGOEHEEEeOgO..",
        "..OGOEEHEeeOgO..",
        "..OGOEEEeeeOgO..",
        "..OGOEEeeeeOgO..",
        "..OGgOEeeeOggO..",
        "...OGgOOOOggO...",
        "....OGGgggGO....",
        ".....OOggOO.....",
        "......OGGO......",
        ".......OO.......",
        "................",
    ],
    'relic': [
        "................",
        ".......OO.......",
        "......OGGO......",
        ".....OOggOO.....",
        "....OGEEEEgO....",
        "...OGEHEEEegO...",
        "...OGEEHEeegO...",
        "...OgEEEeeegO...",
        "...OGEEeeeegO...",
        "....OgEeeeeO....",
        ".....OGGGGO.....",
        "....OGgggggO....",
        "...OGGGGGGGgO...",
        "...OOOOOOOOOO...",
        "................",
        "................",
    ],
    'shard': [
        "................",
        "................",
        "........O.......",
        ".......OHO......",
        "...O..OHBbO..O..",
        "..OHO.OBEbO.OHO.",
        "..OBbOOBbdOOBbO.",
        "..OBbbbBbdbbbdO.",
        "..OGGEGGGGEGGgO.",
        "..OGgEeggEegggO.",
        "..OGGGGGGGGGGgO.",
        "...OOOOOOOOOOOO.",
        "................",
        "................",
        "................",
        "................",
    ],
}


def mpal(base, accent, metal='#c8a050', outline='#18151a', highlight=None):
    B = hexrgb(base)
    E = hexrgb(accent)
    G = hexrgb(metal)
    return {
        'O': hexrgb(outline), 'H': hexrgb(highlight) if highlight else shade(B, 1.5), 'B': shade(B, 1.12), 'b': B,
        'd': shade(B, 0.65), 'E': E, 'e': shade(E, 0.65), 'G': G, 'g': shade(G, 0.62), 's': shade(B, 0.45),
    }


TEMPER = ['#7a8088', '#8a929c', '#9aa4b0', '#a8a0c8', '#b8a0d8', '#c8b0e8', '#d8c8f0', '#ece4ff', '#fff4d0']
TEMPER_GLOW = ['#9ab0c8', '#a8c0d8', '#b8d0e8', '#c0a8ff', '#c890ff', '#d8a8ff', '#e8c8ff', '#ffffff', '#ffd84a']
LUSTROUS = ['#3a5aa8', '#3a68c0', '#4a80d8', '#6a5ad8', '#8a4ad8', '#a84ac8', '#c84ab0', '#e05a8a', '#f0b0d0',
            '#fff0c0']
RUNES = {'faded': ('#a89070', '#d8c090'), 'lustrous': ('#c8a050', '#ffe08a'), 'radiant': ('#e0b040', '#fff2a8'),
         'hero': ('#e89a3a', '#fff0c8'), 'sovereign': ('#e8c266', '#ffffff')}


def render(spec, catalog):
    kind, _, variant = spec.partition(':')
    if kind == 'stone' and variant.startswith('temper'):
        t = int(variant[6:]) - 1
        return draw('stone', mpal(TEMPER[t], TEMPER_GLOW[t]))
    if kind == 'stone' and variant.startswith('lustrous'):
        t = int(variant[8:]) - 1
        base = LUSTROUS[t]
        return draw('gem', mpal(base, shade_hex(base, 1.5), highlight='#ffffff'))
    if kind == 'whetstone':
        if variant == 'blank':
            return draw('whetstone', mpal('#8e8a82', '#a8a49c'))
        color = '#' + catalog['affinities'].get(variant, 'D8D2C4')
        return draw('whetstone', mpal('#6e6a64', color))
    if kind == 'rune':
        base, glow = RUNES[variant]
        return draw('rune', mpal(base, glow, highlight='#ffffff'))
    if kind == 'flask':
        liquid = '#e0602a' if variant == 'ember' else '#3a7ae8'
        return draw('flask', mpal('#c8d8e0', liquid, metal='#8a6a3a', highlight='#ffffff'))
    if kind == 'seed':
        return draw('seed', mpal('#e8c266', '#fff0a8', metal='#5a8a3a'))
    if kind == 'tear':
        if variant == 'rebirth':
            return draw('tear', mpal('#b090e0', '#e0c8ff', highlight='#ffffff'))
        return draw('tear', mpal('#e8f0f8', '#fff6c8', highlight='#ffffff'))
    if kind == 'fragment':
        return draw('fragment', mpal('#c8c4b8', '#c0262e', metal='#c8a050'))
    if kind in ('sigil', 'relic', 'shard'):
        boss = next(b for b in catalog['bosses'] if b['id'] == variant)
        color = '#' + boss['color']
        metal = '#c8a050' if kind != 'sigil' else '#8a8478'
        return draw(kind, mpal('#d8d0c0', color, metal=metal, highlight='#ffffff'))
    raise ValueError('unknown material texture ' + spec)


def shade_hex(h, f):
    r, g, b, _ = shade(hexrgb(h), f)
    return '#%02x%02x%02x' % (r, g, b)


def draw(name, pal):
    rows = M_TEMPLATES[name]
    img = Image.new('RGBA', (SIZE, SIZE), (0, 0, 0, 0))
    px = img.load()
    for y, row in enumerate(rows):
        if len(row) != SIZE:
            raise ValueError(f'material template {name} row {y} has {len(row)} columns')
        for x, ch in enumerate(row):
            if ch != '.':
                px[x, y] = pal[ch]
    return img
