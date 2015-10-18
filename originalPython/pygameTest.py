import random as rng
import numpy as np
import sys
import grid
import math

aStarInteractive = False
algorithm = 5
userStartX = 0
userStartY = 0
userGoalX = 0
userGoalY = 0
g = grid.Grid()
g.readFile("world.txt")
userStartX = int(input("Enter the X coordinate of the start state."))
userStartY = int(input("Enter the Y coordinate of the start state."))
userGoalX = int(input("Enter the X coordinate of the goal state."))
userGoalY = int(input("Enter the Y coordinate of the start state."))
g.qLearning((userStartX, userStartY), (userGoalX,userGoalY))

