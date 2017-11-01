# Finding Rough Set Reducts with Ant Colony Optimization
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

0.4.0 - 18.08.2017

Added simple "dynamic color change" for edges in UI
- works quite good when doing single iterations or full reduct computation

0.4.1 - 20.08.2017

Added mutual information calculation (step 2 in RSFSACO) for all attributes
- in next version function should be able to calculate it for chosen attributes
- there might be problem with threads, when sorting dataset by chosen attributes

0.4.2 - 22.08.2017

Added comparator that sorts by specified attributes - it's required for RSFSACO algorithm.
- also fixed bug when the core contains all attributes so dataset can't be reduced

0.4.3 - 23.08.2017

Exported functionality of both types of ants to the abstract class so it can be easily switched during execution of program
- the same will happen with the logic classes, but first I need to fully implement the second algorithm

0.5.0 - 26.08.2017

Added second algorithm - Mushroom dataset works much smoother than with the first one
- some issues with NaN double values to be investigated
- significance of heuristic is too high (should be like that or no?) or pheromones are too weak
- proper tests should be performed with more datasets

0.5.1 - 27.08.2017

Refactored ant classes a bit
- methods similar for both ants are now in abstract class InterfaceAnt
- same goes with fields that are similar for both ants

The same should be done to logic classes - similar abstract class and proper two extending it
- need to find out why previewing examples doesn't work (textarea with objects is disabled right now)

0.5.2 - 27.08.2017

Cleaned logic classes - abstract Logic class and two extending - JensenLogic and ChineseLogic.

Things to do within next weeks:
1. fix the first algorithm memory leaks (different way for checking if features are a reduct)
2. setting JSACO or RSFSACO in options should really modify the chosen algorithm
3. show the core attributes on the "canvas"
4. add core attributes to reduct results when previewing after iteration or full reduct computation
5. add time estimation?
6. add modification in subset evaluation to limit ants that affect on pheromone trails (to be considered)

0.5.3 - 29.08.2017

Done with issue 2.

0.5.4 - 31.08.2017

Done with issue 4.

0.5.5 - 01.09.2017

Done with issue 3. View examples window has to be fixed.

0.5.6 - 02.09.2017

Done with issue 1. When using RSFSACO, it should be checked if core is already a reduct.
- would be cool to show on the graph which vertices belong to reduct in current iteration

0.5.7 - 02.09.2017

Added simple time measurement (issue 5). Next step is to convert datasets to CSV and add possibility to load them via GUI (new option in menu).

0.5.8 - 03.09.2017

Added all datasets used in RSFSACO algorithm, found two more problems:
- first label (Core) should have the width of longest in the stackpane (vote.csv)
- in case of bigger cores the view is really bad (chess-king.csv)

0.6.0 - 05.09.2017

Added kind of sorting edges to move the best ones to front of graph
Also added different evaluation for first iteration (now not every one ant will find solution in first iteration)
- still need to resolve issue with label width (maybe scrollview, keeping border around VBox now)
- same with too big core to display (maybe scrollview here)
- choosing dataset from examples (all placed in examples directory)
- how to deal with unknown values

0.6.1 - 10.09.2017

- added fruitless searches as mentioned in chinese algorithm
- added "core is reduct" window that indicates there's no need to compute reduct since core is reduct
- issue with updating labels that belong at the moment to reduct is fixed
- added opening sample datasets

0.6.2 - 17.09.2017

- refactored code in main controller (one function for FXML loader)
- refactored code in ~Ant classes (one function was identical in both Jensen and Chinese)
- fixed titles for every new window

To do:
- disabling reduct calculation when it's a core
- added button for reseting graph to the state before reduct computation began

0.6.3 - 17.09.2017

- changed graph to take a little more space, though it's still not cool to preview big datasets
- changed perturbation for initial pheromone from 0.1 to 0.01
- disabled reduct calcuation when it's a core of reduct was found
- added resetting algorithm button and its fucnctionality

0.6.4 - 17.09.2017

Restored "View dataset" functionality with additional buttons to navigate to prevent from too big memory usage by tableview

0.6.5 - 24.09.2017

Second refactor of the code, especially removing "commented methods"

0.6.6 - 06.10.2017

More and more doubts about core-searching in specific cases.

0.6.7 - 08.10.2017

Added FEATURECORE but code needs refactoring, a lot of refactoring.

0.7.0 - 28.10.2017

Migrated project to IntelliJ IDE, used "Inspect code" to refactor a lot of code, but still few methods can be improved

0.7.1 - 28.10.2017

Scrapped Core CT algorithm - it is not used anymore

0.7.2 - 29.10.2017

Refactored code associated with logic, especially calculating conditional entropy

0.7.3 - 01.11.2017

Completely refactored the structure of app so it's fully openable in IntelliJ.