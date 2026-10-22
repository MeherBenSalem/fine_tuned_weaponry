"""Pixel-art sprite engine for Runeborn item textures.

Weapons are drawn either from hand-authored 16x16 templates (letters are palette roles) or by a
parametric diagonal-blade renderer, then coloured with a named palette. Keeping both in one place
gives every sprite the same outline weight, light direction and shading.

Palette roles
  O outline      B blade light   b blade mid    d blade dark   H highlight
  G guard        g guard dark    W grip         w grip dark    P pommel
  E accent/gem   e accent dark   S secondary metal             s secondary dark
"""
from PIL import Image

SIZE = 16


def hexrgb(h):
    h = h.lstrip('#')
    return tuple(int(h[i:i + 2], 16) for i in (0, 2, 4)) + (255,)


def shade(rgb, f):
    r, g, b, a = rgb
    if f >= 1:
        return (min(255, int(r + (255 - r) * (f - 1))), min(255, int(g + (255 - g) * (f - 1))),
                min(255, int(b + (255 - b) * (f - 1))), a)
    return (int(r * f), int(g * f), int(b * f), a)


def palette(blade, guard='#8a6a3a', grip='#5b3a24', accent='#c9d7e3', outline='#1b1820', metal=None, pommel=None,
            highlight=None):
    """Builds a full role palette from a few key colours."""
    B = hexrgb(blade)
    G = hexrgb(guard)
    W = hexrgb(grip)
    E = hexrgb(accent)
    S = hexrgb(metal) if metal else G
    return {
        'O': hexrgb(outline),
        'H': hexrgb(highlight) if highlight else shade(B, 1.55),
        'B': shade(B, 1.12),
        'b': B,
        'd': shade(B, 0.68),
        'G': G,
        'g': shade(G, 0.62),
        'W': W,
        'w': shade(W, 0.65),
        'P': hexrgb(pommel) if pommel else shade(G, 1.1),
        'E': E,
        'e': shade(E, 0.62),
        'S': S,
        's': shade(S, 0.62),
    }


PALETTES = {
    'steel': palette('#c4ccd4', guard='#8a6a3a', grip='#5b3a24'),
    'iron': palette('#a8adb2', guard='#5f6366', grip='#4a3424', pommel='#7a7e82'),
    'rust': palette('#9a6a4e', guard='#5a4a40', grip='#4a3020', accent='#c08050'),
    'crimson': palette('#c8ccd2', guard='#6a1e22', grip='#3a1418', accent='#d0262e', pommel='#b3262b'),
    'glint': palette('#a8c8f0', guard='#3a4a7a', grip='#2a2a48', accent='#a8e0ff', highlight='#f0fbff'),
    'venom': palette('#98c078', guard='#3a4a2a', grip='#2a3020', accent='#9ae85a'),
    'shadow': palette('#4e4866', guard='#24202e', grip='#18141e', accent='#b07ae8', outline='#0c0a10'),
    'ember': palette('#e89a5a', guard='#7a3a1e', grip='#3a2018', accent='#ffd27a', highlight='#fff0c0'),
    'frost': palette('#b8e0f5', guard='#5a7a9a', grip='#2e3e58', accent='#e0f8ff', highlight='#ffffff'),
    'sun': palette('#f0dc9a', guard='#d8aa4a', grip='#6a4a2a', accent='#fff4b8', highlight='#ffffff'),
    'gold': palette('#e6c470', guard='#b8902e', grip='#5a3a24', accent='#fff0a8'),
    'blight': palette('#c08a62', guard='#5a3424', grip='#3a2018', accent='#e07a3c'),
    'brass': palette('#c8ccd2', guard='#c8a050', grip='#5b3a24', pommel='#d8b060'),
    'magma': palette('#3e302c', guard='#2a1e1c', grip='#1e1414', accent='#ff7a2a', highlight='#ff9a4a',
                     outline='#120a08'),
    'mist': palette('#b8c4e0', guard='#4a5478', grip='#2a2e48', accent='#c8d8ff'),
    'rime': palette('#d8f0fa', guard='#2a4a7a', grip='#1c2a44', accent='#ffffff', highlight='#ffffff'),
    'eclipse': palette('#e8d8a0', guard='#4a3a6a', grip='#2a2040', accent='#b07ae8', metal='#4a3a6a'),
    'ash': palette('#5e5650', guard='#2a2624', grip='#1e1a18', accent='#ff8a3a', highlight='#9a8e84',
                   outline='#0e0c0b'),
    'stone': palette('#8e8a82', guard='#4e4a44', grip='#3a3028', accent='#b8b4aa'),
    'star': palette('#2c2c50', guard='#1a1a33', grip='#14142a', accent='#c8d4ff', highlight='#ffffff',
                    outline='#06060e'),
    'crown': palette('#dcd8cc', guard='#e0b858', grip='#5a2a2a', accent='#c0262e', pommel='#e8c266'),
    'bone': palette('#e0d6c0', guard='#b8aa8a', grip='#6a5a44', accent='#f4ecdc'),
    'graft': palette('#a88c7c', guard='#6a3a30', grip='#3e2420', accent='#c0262e', metal='#8a8e92'),
    'wood': palette('#9a6c42', guard='#6a4a2e', grip='#5a3c24', accent='#c8a070'),
    'holy': palette('#f6eed6', guard='#d8b860', grip='#7a5a34', accent='#fff6c8', highlight='#ffffff'),
    'storm': palette('#b8c8ec', guard='#3a4a7a', grip='#2a2e48', accent='#ffe45a', highlight='#ffffff'),
    'dusk': palette('#5e62a0', guard='#2c2f52', grip='#1c1e36', accent='#d0d0ff'),
    'wyrm': palette('#a8d4b0', guard='#4a7a5a', grip='#2a3a30', accent='#ffd84a'),
    'thorn': palette('#7a8a44', guard='#4a5428', grip='#3a2a1a', accent='#c0262e'),
    'astral': palette('#c8d8f0', guard='#5a4030', grip='#4a3424', accent='#6fa8e8', highlight='#e0f4ff'),
    'crystal': palette('#c8e8ff', guard='#4a5a7a', grip='#3a3a50', accent='#9fd8ff', highlight='#ffffff'),
    'void': palette('#5a4a7a', guard='#2a2236', grip='#221c2c', accent='#9a5ae8', highlight='#e0c8ff'),
    'abyss': palette('#2a3050', guard='#141828', grip='#10121e', accent='#4a6aff', highlight='#c8d4ff',
                     outline='#05060c'),
    'pilgrim': palette('#d8c49a', guard='#c8a050', grip='#7a5434', accent='#e8d08a'),
    'flame': palette('#e8a050', guard='#c87a2a', grip='#6a2e1e', accent='#ff6a2a', highlight='#ffe0a0'),
    'blood': palette('#9a2a2e', guard='#5a1418', grip='#3a0e10', accent='#e0303a', highlight='#ff8a8a'),
    'dragon': palette('#8fc7a0', guard='#d8aa4a', grip='#3a4a30', accent='#ffd84a'),
    'none': palette('#808080'),
}

# --------------------------------------------------------------------------------------------
# Hand-authored templates (16 rows of 16 columns). '.' is transparent.

TEMPLATES = {
    'dagger': [
        "................",
        "................",
        "................",
        "................",
        "................",
        "...........OO...",
        "..........OHBO..",
        ".........OHBdO..",
        "........OHBdO...",
        ".......OHBdO....",
        "...OO.OHBdO.....",
        "...OGOObdO......",
        "....OGgOO.......",
        "...OwOGgO.......",
        "..OPwO.OO.......",
        "..OOO...........",
    ],
    'kris': [
        "................",
        "................",
        "................",
        "............O...",
        "...........OHO..",
        "..........OBdO..",
        "..........OHbO..",
        ".........OBdO...",
        "........OHbdO...",
        "........OBdO....",
        "...OO..OHbO.....",
        "...OGOOEdO......",
        "....OGgOO.......",
        "...OwOGgO.......",
        "..OPwO.OO.......",
        "..OOO...........",
    ],
    'stiletto': [
        "................",
        "................",
        "............OO..",
        "...........OHO..",
        "..........OHdO..",
        ".........OHdO...",
        "........OHdO....",
        ".......OHdO.....",
        "......OHdO......",
        "...OO.OEO.......",
        "...OGOOO........",
        "....OGgO........",
        "...OwOGgO.......",
        "..OwO.OO........",
        ".OPO............",
        ".OO.............",
    ],
    'axe': [
        "................",
        "........OOOO....",
        ".......OBbbdO...",
        "......OHBbbddO..",
        "......OBBbbddO..",
        ".......OBbdgSO..",
        "........OOgsO...",
        ".........OwgO...",
        "........OwWO....",
        ".......OwWO.....",
        "......OwWO......",
        ".....OwWO.......",
        "....OwWO........",
        "...OwWO.........",
        "..OPWO..........",
        "..OOO...........",
    ],
    'axe_bone': [
        "................",
        ".......OOO......",
        "......OBHBO.....",
        ".....OBbOBbOO...",
        ".....OBbbBbddO..",
        "......OBbbddgO..",
        ".......OObgsO...",
        ".........OwgO...",
        "........OwWO....",
        ".......OwWO.....",
        "......OwWO......",
        ".....OwWO.......",
        "....OwWO........",
        "...OwWO.........",
        "..OPWO..........",
        "..OOO...........",
    ],
    'greataxe': [
        ".......OOOO.....",
        "......OHBbbOO...",
        ".....OHBBbbddO..",
        ".....OBBbbbddO..",
        ".....OBBbbbddO..",
        "......OBbbdgSO..",
        ".......OObggsOO.",
        "........OwgOBdO.",
        ".......OwWO.OO..",
        "......OwWO......",
        ".....OwWO.......",
        "....OwWO........",
        "...OwWO.........",
        "..OwWO..........",
        ".OPWO...........",
        ".OOO............",
    ],
    'club': [
        "................",
        "..........OOO...",
        ".........OBBbO..",
        "........OBHBbdO.",
        "........OBBbbdO.",
        ".......OBBbbdO..",
        "......OBbbddO...",
        ".....ObbbddO....",
        "....ObbddOO.....",
        "...OwbdO........",
        "...OWwO.........",
        "..OWwO..........",
        "..OwO...........",
        ".OWO............",
        ".OO.............",
        "................",
    ],
    'mace': [
        "................",
        "..........OOO...",
        ".........OSHSO..",
        "........OSBSbSO.",
        "........OHBbbsO.",
        "........OSbbssO.",
        ".........OSssO..",
        "........OgOOO...",
        ".......OwgO.....",
        "......OwWO......",
        ".....OwWO.......",
        "....OwWO........",
        "...OwWO.........",
        "..OGWO..........",
        "..OPO...........",
        "..OO............",
    ],
    'morningstar': [
        "...........O....",
        "........O.OBO.O.",
        ".........OBHBO..",
        "........OBHbbbO.",
        ".......OBBbbbbdO",
        "........ObbbddO.",
        ".........OdddO..",
        "........OgOdO.O.",
        ".......OwgO.O...",
        "......OwWO......",
        ".....OwWO.......",
        "....OwWO........",
        "...OwWO.........",
        "..OGWO..........",
        "..OPO...........",
        "..OO............",
    ],
    'greathammer': [
        "........OOO.....",
        ".......OBHBO....",
        "......OBHBbbO...",
        ".....OBBbbbbdO..",
        "....OBBbbbbbddO.",
        ".....OBbbbbddO..",
        "......ObbbddO...",
        ".......OddgO....",
        "........OwgO....",
        ".......OwWO.....",
        "......OwWO......",
        ".....OwWO.......",
        "....OwWO........",
        "...OwWO.........",
        "..OPWO..........",
        "..OOO...........",
    ],
    'spear': [
        "..............O.",
        ".............OHO",
        "............OHBO",
        "...........OHbdO",
        "..........OBbdO.",
        ".........OGdOO..",
        "........OwGO....",
        ".......OwWO.....",
        "......OwWO......",
        ".....OwWO.......",
        "....OwWO........",
        "...OwWO.........",
        "..OwWO..........",
        ".OwWO...........",
        "OPWO............",
        "OOO.............",
    ],
    'pike': [
        ".............OO.",
        "............OHHO",
        "...........OHBdO",
        "..........OHBdO.",
        "........OOEBdO..",
        ".......OEeOdO...",
        "........OwEO....",
        ".......OwWO.....",
        "......OwWO......",
        ".....OwWO.......",
        "....OwEO........",
        "...OwWO.........",
        "..OwWO..........",
        ".OwWO...........",
        "OPWO............",
        "OOO.............",
    ],
    'halberd': [
        ".............OO.",
        "............OHBO",
        "........OO.OHBO.",
        ".......OBBOOBdO.",
        "......OHBbbbdO..",
        ".......OBbbdGO..",
        "........OOdgO...",
        ".......OwWOO....",
        "......OwWO......",
        ".....OwWO.......",
        "....OwWO........",
        "...OwWO.........",
        "..OwWO..........",
        ".OwWO...........",
        "OPWO............",
        "OOO.............",
    ],
    'glaive': [
        "..........OOOO..",
        ".........OHBBdO.",
        "........OHBbdO..",
        ".......OHBbdO...",
        "......OBbbdO....",
        ".....OEbddO.....",
        "....OGEgOO......",
        "...OwGgO........",
        "..OwWO..........",
        ".OwWO...........",
        "OwWO............",
        "OWO.............",
        "OO..............",
        "................",
        "................",
        "................",
    ],
    'scythe': [
        "......OOOOOO....",
        "....OOHBBbbdOO..",
        "...OHBOOOOObdGO.",
        "..OBO......OgSO.",
        "..OO.......OwgO.",
        "..........OwWO..",
        ".........OwWO...",
        "........OwWO....",
        ".......OwWO.....",
        "......OwWO......",
        ".....OwWO.......",
        "....OwWO........",
        "...OwWO.........",
        "..OwWO..........",
        ".OPWO...........",
        ".OOO............",
    ],
    'claw': [
        "................",
        "................",
        "...O...O...O....",
        "..OHO.OHO.OHO...",
        "..OBO.OBO.OBO...",
        "..OBdOOBdOOBdO..",
        "...OBdOBdOOBdO..",
        "...OSbdSbdSbdO..",
        "...OSSSSSSSSsO..",
        "...OWwWwWwWwsO..",
        "...OWwWwWwWwO...",
        "...OSsSsSsSsO...",
        "....OOOOOOOO....",
        "................",
        "................",
        "................",
    ],
    'knuckles': [
        "................",
        "................",
        "................",
        "....OOOOOOOO....",
        "...OHBOHBOHBO...",
        "...OBdOBdOBdOO..",
        "...ObdObdObdOdO.",
        "...OOOOOOOOObdO.",
        "....OWwWwWwWOdO.",
        "....OWwWwWwWOO..",
        "....OSsSsSsSO...",
        ".....OOOOOOO....",
        "................",
        "................",
        "................",
        "................",
    ],
    'whip': [
        "................",
        "......OOOO......",
        ".....ObEbbO.....",
        "....ObO..ObO....",
        "...ObO....ObO...",
        "...OEO....OEO...",
        "...ObO...ObO....",
        "....ObO.ObO.....",
        ".....ObObO......",
        "......ObO.......",
        ".....OwGO.......",
        "....OwWO........",
        "...OwWO.........",
        "..OwWO..........",
        ".OPWO...........",
        ".OOO............",
    ],
    'staff': [
        "...........OOO..",
        "..........OEHEO.",
        ".........OEHEeO.",
        ".........OEEeeO.",
        "........OGOeOO..",
        ".......OgGO.....",
        "......OwWO......",
        ".....OwWO.......",
        "....OwWO........",
        "....OWO.........",
        "...OwWO.........",
        "..OwWO..........",
        "..OWO...........",
        ".OwWO...........",
        ".OGO............",
        ".OO.............",
    ],
    'staff_crystal': [
        "............OO..",
        "...........OHEO.",
        "..........OHEeO.",
        ".........OHEeO..",
        ".....OO.OEEeO...",
        "....OEEOOeeO....",
        ".....OeEgGO.....",
        "......OwWO......",
        ".....OwWO.......",
        "....OwWO........",
        "....OWO.........",
        "...OwWO.........",
        "..OwWO..........",
        "..OWO...........",
        ".OGO............",
        ".OO.............",
    ],
    'staff_orb': [
        "..........OOOO..",
        ".........OEHEEO.",
        "........OEHEEeeO",
        "........OEEEeeeO",
        "........OEEeeeeO",
        ".........OSeesO.",
        "........OSgOOO..",
        ".......OwSO.....",
        "......OwWO......",
        ".....OwWO.......",
        "....OwWO........",
        "...OwWO.........",
        "..OwWO..........",
        "..OWO...........",
        ".OGO............",
        ".OO.............",
    ],
    'rod': [
        "...........OO...",
        "..........OHEO..",
        "..........OEeO..",
        "........OOOeOO..",
        ".......OGEOOGO..",
        "........OgGgO...",
        ".......OwWOO....",
        "......OwWO......",
        ".....OwWO.......",
        "....OwWO........",
        "...OGWO.........",
        "...OwO..........",
        "..OwO...........",
        ".OGO............",
        ".OO.............",
        "................",
    ],
    'seal': [
        "................",
        "......OOOO......",
        "....OOGGGGOO....",
        "...OGGgOOgGGO...",
        "...OGgOEEOgGO...",
        "..OGgOEHEEOgGO..",
        "..OGOEHEEeeOGO..",
        "..OGOEEEeeeOGO..",
        "..OGgOEeeeOgGO..",
        "...OGgOeeOgGO...",
        "...OGGgOOgGGO...",
        "....OOGGGGOO....",
        "......OWWO......",
        "......OWwO......",
        "......OwWO......",
        ".......OO.......",
    ],
    'totem': [
        "......OOOO......",
        ".....OGEEGO.....",
        "....OGEHEeGO....",
        "...OGOEEeeOGO...",
        "..OGO.OeeO.OGO..",
        "..OO.OGGGGO.OO..",
        ".....OGggGO.....",
        "......OSSO......",
        "......OWwO......",
        "......OwWO......",
        "......OWwO......",
        "......OwWO......",
        "......OGgO......",
        "......OEeO......",
        "......OGgO......",
        ".......OO.......",
    ],
}

# --------------------------------------------------------------------------------------------
# Parametric diagonal blades. Centreline runs bottom-left to top-right along x + y = 15.


def blade(length, width=2, guard=3, grip=3, curve=0.0, tip=2, fuller=False, pommel=True, guard_style='bar',
          start=None, serrated=False, runes=False):
    """Returns a 16x16 template (list of strings) for a diagonal sword.

    The centreline runs from the pommel (bottom-left) to the tip (top-right). Width grows towards
    the lower-right; the upper-left edge catches the light.
    """
    grid = [['.'] * SIZE for _ in range(SIZE)]

    def put(x, y, ch, force=True):
        if 0 <= x < SIZE and 0 <= y < SIZE and (force or grid[y][x] == '.'):
            grid[y][x] = ch

    total = grip + 1 + length
    x0 = start if start is not None else max(1, 15 - total - 1)
    y0 = 15 - x0
    k_guard = grip
    last_bend = 0
    for k in range(total + 1):
        x = x0 + k
        y = y0 - k
        if k < k_guard:
            put(x, y, 'W' if k % 2 == 0 else 'w')
            if k == 0 and pommel:
                put(x - 1, y + 1, 'P')
        elif k == k_guard:
            if guard_style == 'bar':
                for s in range(-guard, guard + 1):
                    put(x - s, y - s, 'G' if s <= 0 else 'g')
                put(x - guard - 1, y - guard - 1, 'E')
                put(x + guard + 1, y + guard + 1, 'e')
            elif guard_style == 'disc':
                for dx, dy in ((0, 0), (-1, 0), (0, 1), (1, 0), (0, -1), (-1, -1), (1, 1)):
                    put(x + dx, y + dy, 'G' if dx + dy <= 0 else 'g')
            elif guard_style == 'cup':
                for dx, dy in ((0, 0), (-1, 0), (0, 1), (-1, 1), (-2, 0), (0, 2), (1, 0), (0, -1)):
                    put(x + dx, y + dy, 'G' if dx + dy <= 0 else 'g')
            elif guard_style == 'wing':
                for s in range(-guard, guard + 1):
                    put(x - s, y - s, 'G' if s <= 0 else 'g')
                put(x - guard, y - guard + 1, 'G')
                put(x + guard - 1, y + guard, 'g')
        else:
            pos = k - k_guard
            remaining = length - pos
            bend = int(round(curve * ((pos / max(1, length)) ** 2) * 3))
            last_bend = bend
            w = width
            if remaining < tip:
                w = max(1, width - (tip - remaining))
            for i in range(w):
                off = i - (w - 1) // 2
                # Curved blades sweep upward: shifting x keeps every step 4-connected.
                cx = x - bend + (off if off > 0 else 0)
                cy = y + (-off if off < 0 else 0)
                if w == 1:
                    ch = 'H' if remaining == 0 else 'B'
                elif i == 0:
                    ch = 'H' if (pos % 3 == 1 and remaining > 1) else 'B'
                elif i == w - 1:
                    ch = 'd'
                    if runes and pos % 3 == 2 and remaining > 1 and w == 2:
                        ch = 'E'
                else:
                    ch = 'b'
                    if (fuller or runes) and 1 < pos < length - 1:
                        ch = 'E' if runes else 'd'
                put(cx, cy, ch)
                if serrated and i == w - 1 and pos % 2 == 0 and remaining > 1:
                    put(cx + 1, cy + 1, 'd', force=False)
    return outline(grid)


def twinblade(length=5, width=2):
    grid = [['.'] * SIZE for _ in range(SIZE)]
    mid = 8
    for k in range(-3, 4):
        x, y = mid + k, 15 - (mid + k)
        grid[y][x] = 'W' if k % 2 == 0 else 'w'
    for sign in (1, -1):
        gk = 4 * sign
        x, y = mid + gk, 15 - (mid + gk)
        for s in range(-2, 3):
            if 0 <= x - s < SIZE and 0 <= y - s < SIZE:
                grid[y - s][x - s] = 'G' if s <= 0 else 'g'
        for p in range(1, length + 1):
            k = gk + p * sign
            x, y = mid + k, 15 - (mid + k)
            remaining = length - p
            w = width if remaining >= 1 else 1
            for i in range(w):
                cx, cy = x + (i if sign > 0 else 0), y + (0 if sign > 0 else i)
                if 0 <= cx < SIZE and 0 <= cy < SIZE:
                    grid[cy][cx] = 'B' if i == 0 else 'd'
    return outline(grid)


def outline(grid):
    """Adds a 1px dark outline around every filled cell (4-neighbourhood)."""
    out = [row[:] for row in grid]
    for y in range(SIZE):
        for x in range(SIZE):
            if grid[y][x] != '.':
                continue
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < SIZE and 0 <= ny < SIZE and grid[ny][nx] not in ('.', 'O'):
                    out[y][x] = 'O'
                    break
    return [''.join(r) for r in out]


PARAMETRIC = {
    'longsword': lambda r: blade(9, width=2, guard=2, grip=3, runes=r),
    'broadsword': lambda r: blade(8, width=3, guard=2, grip=3, fuller=True, guard_style='wing', runes=r),
    'estoc': lambda r: blade(10, width=1, guard=2, grip=3, tip=1),
    'rapier': lambda r: blade(9, width=1, guard=1, grip=3, tip=1, guard_style='cup'),
    'scimitar': lambda r: blade(8, width=2, guard=1, grip=3, curve=0.9, runes=r),
    'falchion': lambda r: blade(7, width=3, guard=1, grip=3, curve=0.6, runes=r),
    'katana': lambda r: blade(9, width=2, guard=1, grip=4, curve=0.45, guard_style='disc', runes=r),
    'nodachi': lambda r: blade(10, width=2, guard=1, grip=4, curve=0.4, guard_style='disc', start=0, runes=r),
    'greatsword': lambda r: blade(9, width=3, guard=3, grip=3, fuller=True, start=0, runes=r),
    'flamberge': lambda r: blade(9, width=3, guard=3, grip=3, serrated=True, start=0, runes=r),
    'colossal': lambda r: blade(9, width=4, guard=3, grip=2, fuller=True, start=0, tip=3, runes=r),
    'twinblade': lambda r: twinblade(),
}

# Palettes whose blades carry glowing rune marks in the fuller.
RUNED = {'glint', 'frost', 'ember', 'sun', 'storm', 'rime', 'mist', 'eclipse', 'ash', 'magma', 'star', 'holy',
         'crown', 'blight', 'venom', 'crimson', 'shadow', 'dusk', 'wyrm', 'abyss', 'void', 'flame', 'blood'}


def template(name, pal_name='steel'):
    if name in PARAMETRIC:
        return PARAMETRIC[name](pal_name in RUNED)
    return TEMPLATES[name]


def render(name, pal_name, glow=False):
    """Renders a template with a palette into a 16x16 RGBA image."""
    rows = template(name, pal_name)
    pal = PALETTES[pal_name]
    img = Image.new('RGBA', (SIZE, SIZE), (0, 0, 0, 0))
    px = img.load()
    for y, row in enumerate(rows):
        if len(row) != SIZE:
            raise ValueError(f'template {name} row {y} has {len(row)} columns')
        for x, ch in enumerate(row):
            if ch == '.':
                continue
            px[x, y] = pal[ch]
    return img


def scaled(img, factor):
    return img.resize((img.width * factor, img.height * factor), Image.NEAREST)
