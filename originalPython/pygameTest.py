import pygame
import random as rng
import numpy as np
import sys
import grid
import math

class Obstacle:
    #let height/width be the distance from the center
    def __init__(self, x, y, height, width):
        self.xPos = x
        self.yPos = y
        self.height = height
        self.width = width
    def draw(self, screen):
        pygame.draw.polygon(screen, (0,0,0), [(self.xPos-self.width, self.yPos-self.height), (self.xPos-self.width, self.yPos+self.height), (self.xPos+self.width, self.yPos+self.height), (self.xPos+self.width, self.yPos-self.height)])

def drawGoal(xPos,yPos, height, width):
    pygame.draw.polygon(screen, (200,200,0), [(xPos-width, yPos-height), (xPos-width, yPos+height), (xPos+width, yPos+height), (xPos+width, yPos-height)])

def dot(a,b):
    return np.arccos(a[0]*b[0] + a[1]*b[1])

def positionToGridLoc(loc, gridWidth, gridHeight, gridCenterX, gridCenterY): #JOE
    (x,y) = loc
    start = 0
    xLoc = 0
    count = 0
    while 1:
        if(x >= start and x <= start+gridWidth):
            break
        else:
            xLoc += 1
            start += gridWidth
            count+=1
        if count > 100 :
            break
    start = 0
    yLoc = 0
    count = 0
    while 1:
        if(y >= start and y <= start+gridHeight):
            break
        else:
            yLoc += 1
            start += gridHeight
            count+=1
        if count > 100 :
            break
    return (xLoc, yLoc)


def updateGoal(loc, gridWidth, gridHeight, gridCenterX, gridCenterY): #JOE
    (xLoc, yLoc) = positionToGridLoc(loc, gridWidth, gridHeight, gridCenterX, gridCenterY)
    return (xLoc*gridWidth+gridCenterX, yLoc*gridHeight+gridCenterY)

def getNormalOfNextState(nextAStarState, currentPos, gridWidth, gridHeight, gridCenterX, gridCenterY): #JOE
    (x,y) = currentPos
    (xA, yA) = nextAStarState
    print("current is ", currentPos, " next is " , nextAStarState)
    if(xA-x == 0 and yA-y > 0):
        return (0,1) #next state below current state, negative normal is 0,1
    if(xA-x == 0 and yA-y < 0):
        return (0,-1) #next state above current state, negative normal is 0,-1
    if(xA-x > 0 and yA-y == 0):
        return (1,0) #next state right of current state, negative normal is -1,0
    if(xA-x < 0 and yA-y == 0):
        return (-1,0)   
    if(xA-x > 0 and yA-y > 0):
        return (-np.sqrt(2)/2,np.sqrt(2)/2) #next state below current state and right, negative normal is 0,1
    if(xA-x < 0 and yA-y > 0):
        return (np.sqrt(2)/2,np.sqrt(2)/2) #next state below current state and left, negative normal is 0,-1
    if(xA-x > 0 and yA-y < 0):
        return (-np.sqrt(2)/2,-np.sqrt(2)/2) #next state above current state and right, negative normal is -1,0
    if(xA-x > 0 and yA-y < 0):
        return (np.sqrt(2)/2,-np.sqrt(2)/2)    
    else:
        return False
   
aStarInteractive = False
algorithm = 5
userStartX = 0
userStartY = 0
userGoalX = 0 
userGoalY = 0
g = grid.Grid()
g.readFile("world.txt")

while(int(algorithm) != 3 and int(algorithm) != 2 and int(algorithm) != 1):
    algorithm = input("Enter 1 for QLearning, 2 for AStar, or 3 for the AStar GUI.")
    if(int(algorithm) == 3):
        aStarInteractive = True
    else:
        userStartX = int(input("Enter the X coordinate of the start state."))
        userStartY = int(input("Enter the Y coordinate of the start state."))
        userGoalX = int(input("Enter the X coordinate of the goal state.")) 
        userGoalY = int(input("Enter the Y coordinate of the start state."))
if(int(algorithm) == 1):
    g.qLearning((userStartX, userStartY), (userGoalX,userGoalY))
if(int(algorithm) == 2):
    return_paths, gcost, hcost, fcost = g.aStar((userStartX, userStartY), (userGoalX,userGoalY))    #added by yuksel
    print("gcost")
    g.draw_grid(gcost, (userStartX, userStartY),  (userGoalX,userGoalY))
    print("hcost")
    g.draw_grid(hcost, (userStartX, userStartY),  (userGoalX,userGoalY))
    print("fcost")
    g.draw_grid(fcost, (userStartX, userStartY),  (userGoalX,userGoalY))
    print("path")
    g.reconstruct_path(return_paths, (userStartX, userStartY),  (userGoalX,userGoalY))
    print()

 

clock = pygame.time.Clock()
pygame.init()
size = width, height = 144*7, 90*7
screen = pygame.display.set_mode(size)
angle = 0
obstacles = []
aStarPath = []
trail = []
aStarIndex = 1
angleModifier = 5
stopMoving = True
#get number of cells in each dimension
xGrid, yGrid = len(g.world[0]), len(g.world) 
#get the size of each grid cells
gridHeight = height/yGrid
gridWidth = width/xGrid
gridCenterY = gridHeight/2.0
gridCenterX = gridWidth/2.0
xPos, yPos = gridWidth/2, gridHeight/2
goal = (gridCenterX,gridCenterY)
radius = int((.5*min(gridCenterX, gridCenterY)))
clickCount = 0


gridGoal = 0
gridStart = 0
return_paths, gcost, hcost, fcost = 0,0,0,0
    

'''
'''
if(aStarInteractive):
    for i in range(len(g.world)):
        for j in range(len(g.world[i])):
            if(g.world[i][j] == "x"):
                temp = Obstacle((j)*gridWidth+gridCenterX, (i)*gridHeight+gridCenterY, gridCenterY, gridCenterX)
                obstacles.append(temp)
    while 1:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                sys.exit()
        keys = pygame.key.get_pressed()  #checking pressed keys
        if keys[pygame.K_UP]:# or keys[pygame.K_W]:
            yPos -= 1
        if keys[pygame.K_DOWN]:# or keys[pygame.K_S]:
            yPos += 1
        if keys[pygame.K_LEFT]:# or keys[pygame.K_A]:
            angle -= 5
        if keys[pygame.K_RIGHT]:# or keys[pygame.K_D]:
            angle += 5
        #angle += int(rng.randint(-5,5))

        if pygame.mouse.get_pressed()[0] and clickCount == 0:
            stopMoving = False
            trail = []
            pos = pygame.mouse.get_pos()
            gridGoal = positionToGridLoc(pos, gridWidth, gridHeight, gridCenterX, gridCenterY)              #added by yuksel
            gridStart = positionToGridLoc((xPos, yPos), gridWidth, gridHeight, gridCenterX, gridCenterY)    #added by yuksel
            goal = updateGoal(pos, gridWidth, gridHeight, gridCenterX, gridCenterY)
            clickCount = 20
            aStarIndex =1
            return_paths, gcost, hcost, fcost = g.aStar(gridStart, gridGoal)    #added by yuksel
            aStarPath = g.reconstruct_path(return_paths, gridStart, gridGoal)   #added by yuksel
            print(aStarPath)

        if(stopMoving == False):
            direction = [np.cos(angle*np.pi/180), np.sin(angle*np.pi/180)] #JOE
            (aStarX, aStarY) = aStarPath[aStarIndex]
            (myX, myY) = positionToGridLoc((xPos,yPos), gridWidth, gridHeight, gridCenterX, gridCenterY)
            if(aStarX == myX and aStarY == myY): #JOE if they are in the same state, then move to the next state
                if(myX == gridGoal[0] and myY == gridGoal[1]):
                    stopMoving = True
                else:
                    aStarIndex += 1
                    (aStarX, aStarY) = aStarPath[aStarIndex]
            print("my position is ", xPos)
            print("my grid pos is ", myX)
            print("my position is ", yPos)
            print("my grid pos is ", myY)
            print((myX,myY))
            normal = getNormalOfNextState((aStarX,aStarY), (myX,myY), gridWidth, gridHeight, gridCenterX, gridCenterY)
            if(normal == False and myX != gridGoal[0] and myY !=gridGoal[1]):
                gridStart = (myX,myY)
                return_paths, gcost, hcost, fcost = g.aStar(gridStart, gridGoal)    #added by yuksel
                aStarPath = g.reconstruct_path(return_paths, gridStart, gridGoal)   #added by yuksel
                aStarIndex = 2
                print("path is")
                print(aStarPath)
                (aStarX, aStarY) = aStarPath[aStarIndex]

                normal = getNormalOfNextState((aStarX,aStarY), (myX,myY), gridWidth, gridHeight, gridCenterX, gridCenterY)
                

            if(stopMoving == False):
                dirAdded =[np.cos((angle+angleModifier)*np.pi/180), np.sin((angle+angleModifier)*np.pi/180)]
                dirSubtracted = [np.cos((angle-angleModifier)*np.pi/180), np.sin((angle-angleModifier)*np.pi/180)]
                angleAdded = dot(dirAdded, normal)
                angleSub = dot(dirSubtracted, normal)
                angleDir = dot(direction, normal)
                if(angleAdded == min(angleAdded, angleSub, angleDir)):
                    angle += angleModifier
                elif(angleSub == min(angleAdded, angleSub, angleDir)):
                    angle -= angleModifier


        offset = math.sqrt(math.pow(radius,2)/2)
        #xcol, ycol = 0,0
        for x in obstacles:
            if((abs(xPos + radius - (x.xPos - x.width)) < 2) and ((x.yPos - x.height - offset - 2) < yPos < (x.yPos + x.height + offset + 2))):
                xPos = x.xPos - x.width - radius - 2
            if((abs(xPos - radius - (x.xPos + x.width)) < 2) and ((x.yPos - x.height - offset - 2) < yPos < (x.yPos + x.height + offset + 2))):
                xPos = x.xPos + x.width + radius + 2
            if((abs(yPos - radius - (x.yPos + x.height)) < 2) and ((x.xPos - x.width - offset - 2) < xPos < (x.xPos + x.width + offset + 2))):
                yPos = x.yPos + x.height + radius + 2
            if((abs(yPos + radius - (x.yPos - x.height)) < 2) and ((x.xPos - x.width - offset - 2) < xPos < (x.xPos + x.width + offset + 2))):
                yPos = x.yPos - x.height - radius - 2

        if(stopMoving == False):
            #if(xcol == 0):
            xPos += .8*np.cos(angle*np.pi/180)
            #if(ycol == 0):    
            yPos += .8*np.sin(angle*np.pi/180)
            trail.append((xPos,yPos))
            if(xPos+radius > width):
                xPos = width-radius
            if(yPos+radius > height):
                yPos = height-radius
            if(xPos-radius < 0):
                xPos = radius
            if(yPos-radius < 0):
                yPos = radius


        screen.fill((200,200,200))
        if(len(trail) >=2):
            pygame.draw.lines(screen, (0,255,0), False, trail, 2)
        drawGoal(goal[0], goal[1], gridCenterY, gridCenterX)
        pygame.draw.circle(screen, (0,0,255), (int(xPos), int(yPos)), radius, 0)
        for x in obstacles:
            x.draw(screen)
        pygame.draw.line(screen, (255,0,0), (int(xPos),int(yPos)), (int(xPos + np.cos(angle*np.pi/180) * radius), int(yPos + np.sin(angle*np.pi/180)*radius)), 2)
        pygame.display.flip()
        clock.tick(60)

        if(clickCount > 0):
            clickCount -= 1
