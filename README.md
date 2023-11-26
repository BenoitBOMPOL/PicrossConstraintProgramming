# fantastic-eureka
Solving Picross using Constraint-Programming tools

[![Generic badge](https://img.shields.io/badge/PICROSS-DONE-chartreuse.svg)](https://shields.io/)
- Enumerating tuples (independent of other constraints)
- Creating the solver instance for a picross grid

[![Generic badge](https://img.shields.io/badge/PICROSS-FIXME-orange.svg)](https://shields.io/)


[![Generic badge](https://img.shields.io/badge/PICROSS-TODO-informational.svg)](https://shields.io/)
- Adding the _link_ between row and column constraints


[![Generic badge](https://img.shields.io/badge/PICROSS-NEXT-blue.svg)](https://shields.io/)
-  $\ell^{k}_{ij}$ = 1 if the k-th constraint on the i-th row is active at column j, 0 otherwise
-  $c^{k}_{ji}$ = 1 if the k-th constraint on the j-th column is active at row i, 0 otherwise
-  forall i, j, forall k in Constaints(i), $\ell^{k}_{ij} \leq \sum_{k'\in T_{j}} c^{k'}_{ji}$
