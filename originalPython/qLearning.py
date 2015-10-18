import random as rnd

def qLearning(self, start, goal):
    q_table = []
    qInit(q_table, self.world, goal)
    printQTable(q_table)
    printRewards(q_table)
    alpha = 1
    gamma = .8
    for i in range(1000):
        y = rnd.randint(0, len(q_table)-1)
        x = rnd.randint(0, len(q_table[y])-1)
        while(self.world[y][x] == "x" or q_table[y][x].getReward() == 100):
            y = rnd.randint(0, len(q_table)-1)
            x = rnd.randint(0, len(q_table[y])-1)
        episode(self.world, q_table, q_table[y][x], 0, gamma, alpha)
    printQTable(q_table)
    #traverseGrid((0,0),(2,1), q_table, self.world)
    traverseGrid(start,goal, q_table, self.world)

#gets neighbors, updates the reward, and then moves to the next state
#returns if depth exceeded, goal reached, or an invalid state reached
def episode(world, qt, state, depth, gamma, alpha):
    if(depth>150):
        #print("early death")
        return
    (x,y) = state.getPosition()
    if(world[y][x] == "x"):
        print("went to the obstacle")
        return
    if(qt[y][x].getReward() == 100):
        return
    neighbors = state.getActions()#getNeighbors(world, qt, state) # get the list of available states to travel to
    next_state, direction = nextState(qt, neighbors, state) # get the state with the optimal value, and the direction taken to get there
    state.takeAction(direction) # add to the counter for taking the direction
    # part2 = gamma*state.getActionReward(direction)
    # part3 = (1/state.numTaken(direction))
    # part4 = episode(world, qt, next_state, depth+1, gamma)
    # if(part4 == None):
    #     return 0
    # print("Part 2 is "+ str(part2)+ " other part is " + str(part3) + " part 4 is " + str(part4))
    #print("winning states reward is " + str(qt[1][1].getActionReward("right")))
    reward = state.getActionReward(direction) + alpha*(next_state.getReward() + gamma*max([next_state.getActionReward(l) for l in next_state.getActions()]) - state.getActionReward(direction)) # * (1/state.numTaken(direction)) 
    state.setActionReward(direction, max(reward, state.getActionReward(direction)))
    episode(world, qt, next_state, depth+1, gamma, alpha)

#looks u/d/l/r for valid neighbors and adds them to a list
def getNeighbors(world, qt, state):
    neighbors = []
    pos = state.getPosition()
    x = pos[0]
    y = pos[1]
    n = len(qt)
    m = len(qt[y])

    if(x+1 < m and world[y][x+1] != "x"):
        neighbors.append(qt[y][x+1])
    if(x-1 >= 0 and world[y][x-1] != "x"):
        neighbors.append(qt[y][x-1])
    if(y+1 < n and world[y+1][x] != "x"):
        neighbors.append(qt[y+1][x])
    if(y-1 >=0 and world[y-1][x] != "x"):
        neighbors.append(qt[y-1][x])
    
    return neighbors

#given a list of neighboring states, the one with the highest reward is returned
def nextState(qt, neighbors, current):
    direction = 0
    '''max_val = 0
    i = 0
    index = 0
    for x in neighbors:
        if(current.getActionReward(x) > max_val):
            max_val = current.getActionReward(x)
            index = i
        i+=1
    direction = neighbors[index]
    min_val = 10000
    if(max_val == 0):
        i = 0
        index = 0
        for x in neighbors:
            if(current.getActionReward(x) < min_val):
                min_val = current.getActionReward(x)
                index = i
            i+=1
        direction = neighbors[index] # if all directions are 0, pick the path least traveled by  
    if(min_val == 0):  
        index = rnd.randint(0, len(neighbors)-1)
        direction = neighbors[index]'''
    index = rnd.randint(0, len(neighbors)-1)
    direction = neighbors[index]
    next_pos = None
    (x,y) = current.getPosition()
    if(direction == "left"):
        next_pos = qt[y][x-1]
    elif(direction == "right"):
        next_pos = qt[y][x+1]
    elif(direction == "up"):
        next_pos = qt[y-1][x]
    elif(direction == "down"):
        next_pos = qt[y+1][x]
    if(next_pos == None):
        print(direction)
    return next_pos, direction

def qInit(qt, world, goal):
    n = len(world)
    m = len(world[0])   
    for y in range(n):
        ls = []
        for x in range(m):
            temp = State(x,y)
            if(x+1 < m and world[y][x+1] != "x"):
                temp.addAction("right", 0)
            if(x-1 >= 0 and world[y][x-1] != "x"):
                temp.addAction("left", 0)
            if(y+1 < n and world[y+1][x] != "x"):
                temp.addAction("down", 0)
            if(y-1 >=0 and world[y-1][x] != "x"):
                temp.addAction("up", 0)
            #if((x+1 < m and world[y][x+1] == "x") or (x-1 >= 0 and world[y][x-1] == "x") or (y+1 < n and world[y+1][x] == "x") or (y-1 >=0 and world[y-1][x] == "x")):
            #    temp.addAction("invalid", 0)
            #    temp.setReward(-1)    
            if(y == goal[1] and x == goal[0]):
                print("heyyyyyyyyy")
                temp.setReward(100)
            ls.append(temp)
        qt.append(ls)

def printQTable(qt):
    for y in range(len(qt)):
        for x in range(len(qt[y])):
            string = "State " + str(qt[y][x].getPosition()) + "'s Actions: " + str(qt[y][x].getActionDict())
            print(string)

def printRewards(qt):
    for y in range(len(qt)):
        string = ""
        for x in range(len(qt[y])):
            string += str(qt[y][x].getReward()) + " "
        print(string)

def traverseGrid(start, goal, qt, world):
    (xs,ys) = start
    (xg,yg) = goal
    reachedEnd = False
    maxReward = 0
    index = 0
    i = 0
    paths = []
    while(not reachedEnd):
        if(xs == xg and ys == yg):
            reachedEnd = True
            break;
        else:
            neighbors = qt[ys][xs].getActions()
            direction = neighbors[0]
            for x in neighbors:
                reward = qt[ys][xs].getActionReward(x)
                if reward > maxReward:
                    maxReward = reward
                    index = i
                    direction = x
                i+=1
            print("Agent selects direction: ",direction)
            if(rnd.randint(0,9) < 6):
                wanted = direction
                while(direction == wanted and len(neighbors) > 1):
                    pick = rnd.randint(0, len(neighbors)-1)
                    direction = neighbors[pick]
            print("Agent moves in direction: ",direction)
            next_pos = None
            if(direction == "left"):
                next_pos = qt[ys][xs-1]
            elif(direction == "right"):
                next_pos = qt[ys][xs+1]
            elif(direction == "up"):
                next_pos = qt[ys-1][xs]
            elif(direction == "down"):
                next_pos = qt[ys+1][xs]
            print(next_pos.getPosition())
            (xs,ys) = next_pos.getPosition()
            #print("now",xs,ys)
            paths.append((xs,ys))
    return paths


class State:
    def __init__(self, x, y):
        self.position = (x, y)
        self.actions = {}
        self.reward = 0
        self.visited = 0
    def addAction(self,direction, value):
        self.actions[direction] = [value, 0]
    def setActionReward(self,direction,value):
        #print("State " + str(self.position) + "reward for direction " + direction + " is being set to " + str(value))
        self.actions[direction][1] = value
    def getActionReward(self, direction):
        return self.actions[direction][1]
    def takeAction(self, direction):
        if(direction in self.actions.keys()):
            self.actions[direction][0] += 1
        else:
            print(self.actions.keys())
    def numTaken(self, direction):
        if(direction in self.actions.keys()):
            return self.actions[direction][0]
        else:
            print("Invalid direction " + direction)
            return 1
    def getPosition(self):
        return self.position
    def getActions(self):
        return list(self.actions.keys())
    def getActionDict(self):
        return self.actions
    def setReward(self, reward):
        self.reward = reward
    def getReward(self):
        return self.reward


