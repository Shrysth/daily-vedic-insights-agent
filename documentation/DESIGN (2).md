---
name: Vedic Celestial Jyotish
colors:
  surface: '#101320'
  surface-dim: '#101320'
  surface-bright: '#363847'
  surface-container-lowest: '#0a0d1a'
  surface-container-low: '#181b28'
  surface-container: '#1c1f2d'
  surface-container-high: '#262937'
  surface-container-highest: '#313443'
  on-surface: '#e0e1f4'
  on-surface-variant: '#d7c3ae'
  inverse-surface: '#e0e1f4'
  inverse-on-surface: '#2d303e'
  outline: '#9f8e7a'
  outline-variant: '#524534'
  surface-tint: '#ffb955'
  primary: '#ffc880'
  on-primary: '#452b00'
  primary-container: '#f5a623'
  on-primary-container: '#644000'
  inverse-primary: '#835500'
  secondary: '#ffb59a'
  on-secondary: '#5b1b00'
  secondary-container: '#f95e14'
  on-secondary-container: '#4f1700'
  tertiary: '#5beaad'
  on-tertiary: '#003824'
  tertiary-container: '#38cd93'
  on-tertiary-container: '#005237'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#ffddb4'
  primary-fixed-dim: '#ffb955'
  on-primary-fixed: '#291800'
  on-primary-fixed-variant: '#633f00'
  secondary-fixed: '#ffdbcf'
  secondary-fixed-dim: '#ffb59a'
  on-secondary-fixed: '#380d00'
  on-secondary-fixed-variant: '#802a00'
  tertiary-fixed: '#6ffbbe'
  tertiary-fixed-dim: '#4edea3'
  on-tertiary-fixed: '#002113'
  on-tertiary-fixed-variant: '#005236'
  background: '#101320'
  on-background: '#e0e1f4'
  surface-variant: '#313443'
typography:
  display-lg:
    fontFamily: Space Grotesk
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 48px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Space Grotesk
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Space Grotesk
    fontSize: 26px
    fontWeight: '600'
    lineHeight: 34px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Space Grotesk
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
  headline-sm:
    fontFamily: Space Grotesk
    fontSize: 18px
    fontWeight: '500'
    lineHeight: 24px
  body-lg:
    fontFamily: Manrope
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Manrope
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Manrope
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: JetBrains Mono
    fontSize: 13px
    fontWeight: '500'
    lineHeight: 18px
    letterSpacing: 0.04em
  label-md:
    fontFamily: JetBrains Mono
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.06em
  label-sm:
    fontFamily: JetBrains Mono
    fontSize: 10px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.08em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 0.75rem
  gutter-tablet: 1rem
  gutter-desktop: 1.5rem
  margin: 1rem
  margin-tablet: 2rem
  margin-desktop: 3rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system unites ancient Vedic astrology (Jyotish) with modern Android 14 Material Design 3 architecture. The aesthetic embraces an elevated cosmic mystery grounded in rigorous sacred geometry and mathematical astronomical precision. Rather than leaning into generic occult or modern flat trends, the design channels celestial observatory instruments: polished dark surfaces, high-contrast astrological typography, and warm, luminous golden accents reminiscent of radiant brass astrolabes and temple oil lamps (*diyas*).

The emotional tone balances reverence, deep spiritual clarity, and analytical empowerment. Targeted at modern practitioners, seekers, and Vedic scholars navigating complex birth charts (Kundali), planetary transits (Gochar), and timing periods (Dasha), the interface treats sacred data with crystalline readability. Visual treatments integrate Material Design 3 surface hierarchies, cosmic translucent glass sheets, fine constellation lines, and subtle starlight halos, calibrated specifically for prolonged nighttime observation on high-refresh 20:9 displays.

## Colors

The palette draws directly from cosmic nightfall and traditional Vedic chromatic symbolism (Navagraha color associations).

- **Background & Canvas:**
  - `background`: `#0B0E1B` (Absolute midnight cosmic base).
  - `surface-container-low`: `#13182E` (Indigo deep field tier 1).
  - `surface-container-high`: `#1A213D` (Elevated planetary housing tier 2).
  - `surface-container-highest`: `#242C4F` (Active states, tooltips, dialogs).

- **Accents & Sacred Signifiers:**
  - `primary` (`#F5A623`): Radiant Solar Gold (*Surya* / *Brihaspati*). Primary action triggers, selected divisional charts, auspicious highlights, and active astral nodes.
  - `secondary` (`#E65100`): Sacred Temple Saffron (*Agni* / *Mangal*). Secondary CTA emphasis, transit warnings, consecrated markers, and festive muhurta flags.
  - `tertiary` / `auspicious` (`#10B981`): Emerald (*Budha* / *Shubha*). Positive planetary dignities, benefic aspects, Raj Yogas, and favorable time windows.
  - `error` / `inauspicious` (`#EF4444`): Astrological Ruby (*Kuja Dosha* / *Ashubha*). Afflictions, retrograde alerts, malefic combustion (*Asta*), and Sade Sati notifications.

- **Celestial Typography Tiers:**
  - `text-primary`: `#F8FAFC` (High-contrast starlight white).
  - `text-secondary`: `#E2E8F0` (Planetary ring silver-blue).
  - `text-tertiary`: `#94A3B8` (Nebular slate for degrees, house numbers, and ephemeris metadata).
  - `outline-variant`: `rgba(245, 166, 35, 0.16)` (Subtle golden starlight boundaries).

## Typography

The typographic hierarchy coordinates three distinct typefaces to represent the duality of celestial mathematics and esoteric narrative:

1. **Headlines (`Space Grotesk`):** Provides geometric precision, structural authority, and astronomical instrument character. It renders zodiac house titles, planetary names, and screen headers cleanly across dark substrates.
2. **Body (`Manrope`):** Delivers fluid, highly legible prose for astrological interpretations, transit analysis, and classical shloka translations without optical fatigue.
3. **Labels & Data Coordinates (`JetBrains Mono`):** Dedicated to computational Vedic metadata—exact planetary longitude (`23° 41' 12"`), Nakshatra Pada, Vimshottari Dasha spans, and sidereal time calculations.

Numerical alignment must use tabular figures (`tnum`) across all ephemeris tables and divisional chart cells.

## Layout & Spacing

The layout is engineered for tall, narrow screens, optimized around the 20:9 viewport (e.g., 1080x2400 or 720x1600 on Moto G35 5G) while scaling into tablets and desktop dashboards.

- **Mobile (20:9 Viewport):**
  - Uses a 4-column fluid layout with `0.75rem` (12px) gutters and `1rem` (16px) margins.
  - Accounts for edge-to-edge Android 14 gesture insets and top camera punch-holes with safe-area paddings.
  - The South Indian and North Indian Kundali square charts conform to a strict 1:1 aspect ratio, centered with dynamic auto margins.
  - Critical planetary ribbons and bottom navigation anchors sit within thumb-reach zones (lower 40% of the viewport).

- **Tablet & Foldable (>=600dp):**
  - Transitions to an 8-column layout with `1rem` gutters and `2rem` margins. Kundali chart pins to the left side while divisional charts (D9 Navamsha, D10 Dashamsha) and Dasha tables align to the right.

- **Desktop (>=1024dp):**
  - Utilizes a 12-column max-width container (`1280px`) with `1.5rem` gutters and `3rem` margins.

## Elevation & Depth

Visual depth follows the Material Design 3 surface-tier model infused with astral luminescence:

- **Level 0 (Canvas):** Pure `#0B0E1B`. Unlit cosmic background for infinite depth.
- **Level 1 (Card Baseline / `#13182E`):** Indigo field elevated via an ultra-subtle border: `1px solid rgba(245, 166, 35, 0.12)`.
- **Level 2 (Hover / Elevated Kundali Cell / `#1A213D`):** Elevated container paired with an ambient starlight bloom: `box-shadow: 0 4px 20px -2px rgba(11, 14, 27, 0.8), 0 0 16px 0 rgba(245, 166, 35, 0.08)`.
- **Level 3 (Modal Sheets & Time Travelers / `#242C4F`):** Floats above the canvas with a diffused golden halo: `box-shadow: 0 12px 32px -4px rgba(0, 0, 0, 0.7), 0 0 24px 2px rgba(245, 166, 35, 0.14)`.
- **Cosmic Overlays (Glassmorphism):** Floating action bars and live Muhurta sticky headers utilize `backdrop-filter: blur(16px)` over `rgba(19, 24, 46, 0.78)` with a top hairline highlight of `rgba(248, 250, 252, 0.15)`.

## Shapes

The shape system matches Material 3 rounded standards, balancing organic softness with the sacred geometric rigor of Vedic Yantras:

- Standard Cards and Chart Enclosures: `16px` (`rounded-lg` / `1rem`) corner radius.
- Small Modifiers, House Tags, and Ephemeris Badges: `8px` (`rounded` / `0.5rem`).
- Pills, Buttons, Search Bars, and Filter Chips: Full radius (`9999px` / pill-shaped) to provide high tactile contrast against sharp Kundali grid divisions.
- Internal chart quadrants maintain sharp inner junctions (`0px`) surrounded by outer `16px` boundary frames, symbolizing sacred inner enclosures (*Garbhagriha*).

## Components

- **Buttons:**
  - *Primary (Solar):* Fully rounded pill (`h-12` on mobile), filled with `#F5A623`, text in `#0B0E1B` (bold). Features an active-state inner glow `inset 0 0 8px rgba(255, 255, 255, 0.4)`.
  - *Secondary (Saffron / Sacred):* Pill filled with `#E65100`, text in `#F8FAFC`. Used for ritual or urgent astrologic actions (e.g., "Calculate Muhurta").
  - *Outlined (Astral):* Pill with transparent background, `1.5px` border in `rgba(245, 166, 35, 0.35)`, text in `#F8FAFC`. On press, glows with `rgba(245, 166, 35, 0.15)`.

- **Cards:**
  - Standard card features `#13182E` surface, `16px` corner radius, and `1px` stroke of `rgba(245, 166, 35, 0.12)`.
  - Card headers feature `Space Grotesk` Medium in `#F8FAFC` alongside `JetBrains Mono` micro-coordinates in `#94A3B8`.
  - *Highlighted Astral Card:* Adds a radiant golden gradient border along the top edge (`linear-gradient(90deg, transparent, #F5A623, transparent)`).

- **Astrological Indicators & Chips:**
  - *Auspicious Chip:* Background `rgba(16, 185, 129, 0.12)`, border `rgba(16, 185, 129, 0.4)`, text `#10B981` in `JetBrains Mono` uppercase. Prefixed with a mini diamond glyph (`◆`).
  - *Inauspicious Chip:* Background `rgba(239, 68, 68, 0.12)`, border `rgba(239, 68, 68, 0.4)`, text `#EF4444` in `JetBrains Mono`. Prefixed with an alert crest (`▲`).
  - *Planetary Filter Chips:* Material 3 interactive pills (`h-8`), background `#1A213D`, text `#E2E8F0`. When selected, transitions to `#F5A623` with `#0B0E1B` text.

- **Vedic Chart (Kundali Display):**
  - Renders both North Indian (diamond-based) and South Indian (zodiacal box-based) geometries.
  - Outer frame: `16px` rounded container filled with `#13182E`.
  - Dividing lines: Sharp `1px` lines tinted with `rgba(245, 166, 35, 0.25)`.
  - House Numbers: `JetBrains Mono` regular in `rgba(148, 163, 184, 0.5)`.
  - Planetary Glyphs: High-contrast `#F8FAFC` accompanied by superscript degree notation in `#F5A623`. Benefics marked with emerald dots; retrograde planets enclosed in golden parenthesis (e.g., `(Sa)`).

- **Input Fields & Date-Time Pickers:**
  - Outlined Material 3 style: container fill `#0B0E1B`, border `1px solid rgba(226, 232, 240, 0.2)`.
  - Focus state: border shifts to `#F5A623` with subtle golden starlight box shadow (`0 0 10px rgba(245, 166, 35, 0.2)`).
  - Labels float into the border notch, styled in `JetBrains Mono` `#F5A623`.

- **Lists & Dasha Timelines:**
  - Dense tree or vertical timeline layouts with connected starlight nodes.
  - Major Mahadasha node displays a `10px` pulsing golden dot; Antardashas sit on a connected vertical dotted guideline (`#242C4F`).
  - Rows alternate with `rgba(26, 33, 61, 0.4)` on hover/tap.