# pracaInzynierska
First attempts on Ant Colony Optimization solving the problem of finding possibly the best reduct in dataset.

0.0.1 - 29.06.2017__
First attempts on trying to create the interface and kind of logic:
- dataset in .csv can be loaded and showed in textarea

0.0.2 - 29.06.2017__
Cleaning the controller a bit and adding the Logic class for most of the computations and handling loading the file

0.0.3 - 01.07.2017__
First attempts on graph drawing
- next commits going to focus on ant colony rather than GUI

0.1.0 - 09.07.2017__
A kinda huge difference, because there's a working Ant object
- ants find the solution using discrebility matrix
- graphics are abandoned right now, it's gonna be a web application due to drawing graphs issues

0.1.1 - 09.07.2017__
Ant algorithm probably works, although experiments for bigger datasets have to be performed
- it's difficult now to set proper constans for pheromones update, some edges got ultrapheromoned

0.2.0 - 16.07.2017__
Implemented Core-CT and Core-DDM algorithms for finding core in provided dataset
- time elapsed for execution of each algorithms might be calculated to choose the better one
- still not working on graphical side, probably something will change after the last algorithm will b implemented

0.2.1 - 23.07.2017__
Worked a bit on the interface, finally there's a kind of graph with edges and nodes
However, algorithm functionality is still to be anchored to the graphical interface, next week maybe
- nodes are movable and so edges
- right-clicking on edge will show amount of pheromone on the edge
- right-clicking on node will show number of ants on the edge
- more buttons prepared for future work on the GUI

0.2.2 - 25.07.2017__
Created the interface branch for GUI development