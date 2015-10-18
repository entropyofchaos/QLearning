import qLearning

class Grid:
    def __init__(self):
        self.world = []
        self.cols = 0       #added by yuksel
        self.rows = 0       #added by yuksel
        self.walls = []     #added by yuksel
        self.weights = {}   #added by yuksel

    def readFile(self, path):
        with open(path, "r") as ifs:
            for line in ifs:
                ls = [x for x in line.rstrip()]
                self.world.append(ls)
        self.rows = len(self.world)                     #added by yuksel
        self.cols = len(self.world[0])                  #added by yuksel
        for y in range(self.rows):                      #added by yuksel
            for x in range(self.cols):                  #added by yuksel
                if(self.world[y][x] == "x"):                 #added by yuksel
                    self.walls.append((x, y))           #added by yuksel

    def printWorld(self):
        for x in self.world:
            line = ''
            for y in x:
                line += str(y) + " "
            print(line)

    #added by yuksel
    def adjacent(self, loc):
        (x, y) = loc
        adj = []
        if(x+1 < self.cols and (x+1, y) not in self.walls):
            adj.append((x+1, y))
        if(x-1 >= 0 and (x-1, y) not in self.walls):
            adj.append((x-1, y))
        if(y+1 < self.rows and (x, y+1) not in self.walls):
            adj.append((x, y+1))
        if(y-1 >= 0 and (x, y-1) not in self.walls):
            adj.append((x, y-1))
        return adj

    #added by yuksel
    def weight(self, target):
        return self.weights.get(target, 1)


Grid.qLearning = qLearning.qLearning

g = Grid()
g.readFile("world.txt")
g.printWorld()
start = (0,0)                  
goal = (4,4)                   
#g.qLearning(start, goal)

'''return_paths, gcost, hcost, fcost = g.aStar(start, goal)    #added by yuksel

#added by yuksel
print("gcost")
g.draw_grid(gcost, start, goal)
print("hcost")
g.draw_grid(hcost, start, goal)
print("fcost")
g.draw_grid(fcost, start, goal)
print("path")
g.reconstruct_path(return_paths, start, goal)
print()
'''
