# Solving Picross using Constraint-Programming tools

[![Generic badge](https://img.shields.io/badge/PICROSS-DONE-chartreuse.svg)](https://shields.io/)
- Creation of a _generic_ picross instance, loaded with ad-hoc `.px` files.
- For each constraint (tuple, size), enumeration of every solution works.
- Creation of a picross-solver class, taking the `.px` file location as an input
- 😮‍💨 : First version of the solver was using reification (`solver.ifThen`) in order to connect bloc start location and (0/1)-variable indicators
- 🥳 : Last version using a (**wayyyyy**) simpler model
- 🐖 : PIG (**P**icross **I**nstance **G**enerator), small python module creating `.px` instances.
- 🧠 : Writing a **checker**, ensuring every solution is correct.

[![Generic badge](https://img.shields.io/badge/PICROSS-FIXME-orange.svg)](https://shields.io/)
- ⚔️ : Solver works well for middle-size grids (30x30 Picross grids is (yet) way too big, but depends on the number of tuples enumerated, limit seems to be around ~300000 tuples, which can be quickly reached for bigger Picross with _sparse_ constraints.)

[![Generic badge](https://img.shields.io/badge/PICROSS-TODO-informational.svg)](https://shields.io/)
- ↪️ : Propagation of the constraints, looking for the level of consistency of our model.
- 🚅 : Benchmarking : How many nodes are used ? How many tuples were enumerated ?

[![Generic badge](https://img.shields.io/badge/PICROSS-NEXT-8A2BE2.svg)](https://shields.io/)
- ⁉️ : Is there a model with the same consistencies, but using less tuples ?
-   1. This can be done using DFAs (cf. ![Choco Solver](https://choco-solver.org/tutos/nonogram/), however there is no DFA / regular on CPLEX).
- ✍️ : Writing a small report on the whole process (cf. ![Overleaf](https://fr.overleaf.com/read/bxgrsftxdxhn#3cfc43) ).


[![Generic badge](https://img.shields.io/badge/PICROSS-MATHS_NEXT-8A2BE2.svg)](https://shields.io/)
- Algebraic formulation for a picross instance
