# Solving Picross using Constraint-Programming tools

[![Generic badge](https://img.shields.io/badge/PICROSS-DONE-chartreuse.svg)](https://shields.io/)
- Creation of a _generic_ picross instance, loaded with ad-hoc `.px` files.
- For each constraint (tuple, size), enumeration of every solution works.
- Creation of a picross-solver class, taking the `.px` file location as an input

[![Generic badge](https://img.shields.io/badge/PICROSS-FIXME-orange.svg)](https://shields.io/)
- 😮‍💨 : Solver is using reification (`solver.ifThen`) in order to connect bloc start location and (0/1)-variable indicators
  - (Version ![1.0](https://github.com/BenoitBOMPOL/PicrossConstraintProgramming/releases/tag/v1.0) is using reification)
  
- ⚔️ : Solver does not work for middle-size Picross (10x10 Picross grids appear to be the limit)

[![Generic badge](https://img.shields.io/badge/PICROSS-TODO-informational.svg)](https://shields.io/)
- 🧮 : Replacing refication by inequalities (can be done using boolean/MIP-like constraints 🤢)

[![Generic badge](https://img.shields.io/badge/PICROSS-NEXT-8A2BE2.svg)](https://shields.io/)
- ✍️ : Writing a small report on the whole process


## Towards MIP-Like solver
I'll use `ers[i][k]` as a shortcut for `eff_row_solution[i][k]`, and likewise `erc[j][k]` as a shortcut for `eff_col_solution[j][k]`.
The following notations goes (cf. picture)
