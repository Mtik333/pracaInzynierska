# pracaInzynierska
First attempts on Ant Colony Optimization solving the problem of finding possibly the best reduct in dataset.

0.0.1 - 29.06.2017

First attempts on trying to create the interface and kind of logic:
- dataset in .csv can be loaded and showed in textarea

0.0.2 - 29.06.2017

Cleaning the controller a bit and adding the Logic class for most of the computations and handling loading the file

0.0.3 - 01.07.2017

First attempts on graph drawing
- next commits going to focus on ant colony rather than GUI

0.1.0 - 09.07.2017

A kinda huge difference, because there's a working Ant object
- ants find the solution using discrebility matrix
- graphics are abandoned right now, it's gonna be a web application due to drawing graphs issues

0.1.1 - 09.07.2017

Ant algorithm probably works, although experiments for bigger datasets have to be performed
- it's difficult now to set proper constans for pheromones update, some edges got ultrapheromoned

0.2.0 - 16.07.2017

Implemented Core-CT and Core-DDM algorithms for finding core in provided dataset
- time elapsed for execution of each algorithms might be calculated to choose the better one
- still not working on graphical side, probably something will change after the last algorithm will b implemented

0.2.1 - 23.07.2017

Worked a bit on the interface, finally there's a kind of graph with edges and nodes
However, algorithm functionality is still to be anchored to the graphical interface, next week maybe
- nodes are movable and so edges
- right-clicking on edge will show amount of pheromone on the edge
- right-clicking on node will show number of ants on the edge
- more buttons prepared for future work on the GUI

0.2.2 - 25.07.2017

Created the interface branch for GUI development

0.2.3 - 29.07.2017

Minor changes in "view" part of project
- prepared view for ant algorithm settings (no validation at this moment).
- "graph design" is now based on edges/vertices' objects. Thanks to this a window showing pheromone on edge will be easy to make (I hope)

0.2.4 - 30.07.2017

Added very simple GUI for edge and vertice details preview

0.2.5 - 30.07.2017

Removed unused imports in various files, also created file for static Strings

0.3.0 - 30.07.2017

Quite a bit of changes:
- initializing ants randomly works (need to add new option in settings)
- single-step edge selection works (though it's hard to see it in UI now)

To do for next version:
- a screen that informs about found reduct should appear after finding in during one iteration
- single iteration execution
- a screen or anything that shows attributes already picked by ants and statement if it's a solution

0.3.1 - 01.08.2017

Worked on proper "flow" between single step, single iteration and full reduct computation
- fixed minor bugs in the algorithm with wrong iterations performed.
- next version - gonna work on showing results of single step/single iteration/reduct computation

0.3.2 - 02.08.2017

Added screen for one-step operations - after each operation, it shows which attribute ant picked and its current list of attributes
- also it shows if ant already found solution
- if one ant found solution, then basically you cannot do a step again because the iteration has ended and ants are being moved to start

0.3.5 - 12.08.2017

Refactoring the code (used functional operations in most of cases NetBeans hinted), also formatted the code a bit.

0.3.6 - 13.08.2017

Added screen for full algorithm calculation - not many informations but you can preview the reduct of each performed iteration

My next task is either to make second algorithm or change edges colors each iteration so it can be shown which edges are the most "occupied" by ants

0.3.7 - 18.08.2017

Fixed few glitches:
- pheromone's not being updated when no solution is found
- step-by-step ended when first ant found solution - instead should check if iteration exceedes max number
- minor glitches with displaying single step pick