# Chromagica

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

## Project Status

### Code Coverage Sunburst

![Code coverage "sunburst"](https://codecov.io/gh/Daghis/Chromagica/graphs/sunburst.svg?token=3YC2A4GIE7)
