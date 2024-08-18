# Chromagica

[![Java CI with Gradle](https://github.com/Daghis/Chromagica/actions/workflows/gradle.yml/badge.svg)](https://github.com/Daghis/Chromagica/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/Daghis/Chromagica/graph/badge.svg?token=3YC2A4GIE7)](https://codecov.io/gh/Daghis/Chromagica)

Chromagica is an application that collects color data from multiple layers of one color of filament
on top of another. Using the points in 3D colorspace in a quadratic regression, the algorithm
determines quadratic coefficients that can be used to predict how filament colors interact with
any underlying color. These coefficients are stored in a central "filament database" that
contains all filaments loaded.

The color data is collected from [step wedge test prints](https://makerworld.com/en/models/508600)
where you print _n_ colors with _i_ 0.08mm layers. An image of a test print is then loaded into
the filament database using the `LoadStepWedge` application.

<img alt="8-color step wedge print" width="400" src="examples/8x8-color-sample.png"/>

As an example, here are per-component coefficients for Yellow:

- 🔴 = 0.00044𝒓² + 0.39639𝒓 + 123.71899
- 🟢 = 0.00041𝒈² + 0.29920𝒈 + 125.12055
- 🔵 = -0.00045𝒃² + 0.20879𝒃 + 76.19541

This means that if you have a base color of Cyan \[RGB(20, 98, 197)] and add one layer of
Yellow, we can compute the expected red, green, and blue component values of the resulting color
as:

- 🔴 = 132
  (0.00044(20²) + 0.39639(20) + 123.71899)
- 🟢 = 158
  (0.00041(98²) + 0.29920(98) + 125.12055)
- 🔵 = 100
  (-0.00045(197²) + 0.20879(197) + 76.19541)

What this means symbolically:

- applyYelllow(![Cyan swatch](examples/Cyan-Swatch.png))
  = ![Cyan + Yellow swatch](examples/Cyan-Yellow-Swatch.png)

## Project Status

### Code Coverage Sunburst

![Code coverage "sunburst"](https://codecov.io/gh/Daghis/Chromagica/graphs/sunburst.svg?token=3YC2A4GIE7)
