import pygame
import sys, os
import math

pygame.init()

size = width, height = 1280, 720
screen = pygame.display.set_mode(size)
scale = 1

grid_width, grid_height = 50, 25

grid_size = (width) * scale, (height) * scale

grid_thickness = 1

square_size = round((grid_size[0] + grid_size[1]) /(2* grid_width)) * scale
print(square_size)

white = pygame.Color(255,255,255)
black = pygame.Color(0,0,0)
red = pygame.Color(255, 0, 0)
gray = pygame.Color(128, 128, 128)
brown = pygame.Color(165, 42, 42)
green = pygame.Color(0, 255, 0)
pink = pygame.Color(0xFF, 0xC0, 0xCB)

font = pygame.font.Font(None, 30)

grid_lines = pygame.Rect((width-grid_size[0]-grid_width) / 2 - grid_thickness, (height-grid_size[1]-grid_height) / 2 - grid_thickness,
            grid_size[0] + grid_width-1, grid_size[1] + grid_height-1)

blocks = [1, 2, 3, 4]
current_block = 1
types = ["full", "slope45"]
current_type = "full"
rotations = [0, 90, 180, 270]
current_rotation = 0

def rotate_list(l):
    new_l = []
    count = 0
    for item in l:
        if count == 0: count += 1; continue
        new_l.append(item)
        count += 1
    new_l.append(l[0])
    print(l)
    print(new_l)
    return new_l

def change_current_block():
    global current_block, blocks
    blocks = rotate_list(blocks)
    current_block = blocks[0]

def change_current_type():
    global current_type, types
    types = rotate_list(types)
    current_type = types[0]

def change_current_rotation():
    global current_rotation, rotations
    rotations = rotate_list(rotations)
    current_rotation = rotations[0]



class GridSquare:
    def __init__(self, x, y, texture_id, block_type, rotation):
        self.pos = x, y
        self.texture_id = texture_id
        self.block_type = block_type
        self.rotation = rotation # degrees
        self.rect = pygame.Rect(self.pos[0]*square_size, self.pos[1]*square_size, square_size, square_size)
        self.pressed = False

    def change_texture(self, new_texture_id=None):
        if new_texture_id is not None:
            self.texture_id = new_texture_id
        else:
            self.texture_id += 1

    def change_block_type(self, change_to):
        self.block_type = change_to

    def rotate(self, degrees):
        self.rotation += degrees
        if self.rotation >= 360:
            self.rotation = self.rotation % 360

    def set_rotation(self, degrees):
        self.rotation = degrees
        if self.rotation >= 360:
            self.rotation = self.rotation % 360

    def clear(self):
        self.texture_id = 0
        self.block_type = "full"
        self.rotation = 0

    def check(self):
        if self.rect.collidepoint(pygame.mouse.get_pos()):
            if pygame.mouse.get_pressed()[0] == True:
                return True

buttons = []

class Button:
    def __init__(self, x, y, width, height, text, color, func):
        self.pos = x, y
        self.color = color
        self.text = text
        self.rect = pygame.Rect(x, y, width, height)
        self.func = func

    def draw(self):
        global screen
        pygame.draw.rect(screen, self.color, self.rect)
        screen.blit(font.render(self.text, True, black), self.pos)

    def use(self):
        self.func()

    def check(self):
        if self.rect.collidepoint(pygame.mouse.get_pos()):
            if pygame.mouse.get_pressed()[0] == True:
                return True

current_grid=[]
gridint = 0

grid_squares = []
grid_entities = []
grid_etc = []
for i in range(grid_height):
    grid_squares.append([])
    grid_entities.append([])
    grid_etc.append([])
    for j in range(grid_width):
        grid_squares[i].append(GridSquare(j, i, 0, "full", 0)) # make array of clear squares
        grid_entities[i].append(GridSquare(j, i, 0, "", 0)) #pos, type
        grid_etc[i].append(GridSquare(j, i, 0, "", 0))

row = 0
for i in grid_squares:
    col = 0
    for j in i:
        pt = (width-grid_size[0]-len(grid_squares)) / 2 + (col-1) * grid_thickness + col*square_size, (height-grid_size[1]-len(grid_squares)) / 2 + (row-1) * grid_thickness + row * square_size
        col += 1
    row += 1


current_grid = grid_squares

def save():
    print("Saving...")
    i = 0
    while os.path.exists('./map_%s.json' % i):
        i += 1
    f = open('./map_%s.json' % i, 'w')
    print('./map_%s.json' % i)
    f.write('{\n')
    f.write('"levelWidth": ' + str(grid_width) + ',\n')
    f.write('"levelHeight": ' + str(grid_height) + ',\n')
    f.write('"mapLayout": {\n')
    for i in grid_squares:
        for j in i:
            f.write('"0": {\n')

            f.write('"coords": [' + str(j.pos[0]) + ', ' + str(grid_height - 1 - j.pos[1]) + '],\n')
            f.write('"textureId": ' + str(j.texture_id) + ',\n')
            f.write('"type": "' + j.block_type + '",\n')
            f.write('"rotation": ' + str(j.rotation) + '\n')

            if j.pos[1] == grid_height - 1 and j.pos[0] == grid_width - 1:
                f.write('}\n')
            else:
                f.write('},\n')
    f.write('},\n')
    f.write('"mapEntities": {\n')
    for i in grid_entities:
        for j in i:
            f.write('"0": {\n')

            f.write('"coords": [' + str(j.pos[0]) + ', ' + str(grid_height - 1 - j.pos[1]) + '],\n')
            f.write('"type": "' + str(j.texture_id) + '"\n')

            if j.pos[1] == grid_height - 1 and j.pos[0] == grid_width - 1:
                f.write('}\n')
            else:
                f.write('},\n')
    f.write('},\n')
    f.write('"mapEtc": {\n')
    for i in grid_etc:
        for j in i:
            f.write('"0": {\n')

            f.write('"coords": [' + str(j.pos[0]) + ', ' + str(grid_height - 1 - j.pos[1]) + '],\n')
            f.write('"type": "' + str(j.texture_id) + '"\n')

            if j.pos[1] == grid_height - 1 and j.pos[0] == grid_width - 1:
                f.write('}\n')
            else:
                f.write('},\n')
    f.write('}\n')

    f.write('}')

    f.close()
    print("Saved successfully.")

def change_grid():
    global current_grid, gridint
    if current_grid == grid_squares:
        current_grid = grid_entities
        gridint = 1
    elif current_grid == grid_entities:
        current_grid = grid_etc
        gridint = 2
    else:
        current_grid = grid_squares
        gridint = 0

last_squares = []

button_held = False

changeblockbutton = Button(1050, 50, 150, 20, "change block", white, change_current_block)
savebutton = Button(1050, 80, 50, 20, "save", white, save)
changegridbutton = Button(1050, 110, 150, 20, "change grid", white, change_grid)
changetypebutton = Button(1050, 170, 200, 20, "change block type", white, change_current_type)
rotatebutton = Button(1050, 230, 100, 20, "rotate", white, change_current_rotation)

buttons.append(changeblockbutton)
buttons.append(savebutton)
buttons.append(changegridbutton)
buttons.append(changetypebutton)
buttons.append(rotatebutton)

def draw_slope45(square, color):
    rect = square.rect

    # 0 topleft, 1 bottomleft, 2 bottomright, 3 topright
    pt0 = (rect.x, rect.y)
    pt1 = (rect.x, rect.y + rect.height)
    pt2 = (rect.x + rect.width, rect.y + rect.height)
    pt3 = (rect.x + rect.width, rect.y)

    points = [pt0, pt1, pt2, pt3]

    points.pop(math.floor(square.rotation / 90))

    pygame.draw.polygon(screen, color, points)


while True: #loop
    for event in pygame.event.get():
        if event.type == pygame.QUIT: sys.exit()
        elif event.type == pygame.MOUSEBUTTONDOWN:
            button_held = True
            for button in buttons:
                if button.check():
                    button.use()


    if button_held:
        for i in current_grid:
            for j in i:
                if j.check() and j not in last_squares:
                    j.pressed = not j.pressed
                    if j.pressed:
                        j.change_texture(current_block)
                        j.change_block_type(current_type)
                        j.rotate(current_rotation)
                    else:
                        j.clear()
                    last_squares.append(j)
    else:
        last_squares = []

    button_held = pygame.mouse.get_pressed()[0]

    screen.fill(black)

    pygame.draw.rect(screen, gray, grid_lines)

    for i in current_grid:
        for j in i:
            current = j.rect

            color = white

            if j.pressed:
                if j.texture_id == 0: color = white #empty
                elif j.texture_id == 1: color = gray #stone
                elif j.texture_id == 2: color = green #grass
                elif j.texture_id == 3: color = brown #dirt
                elif j.texture_id == 4: color = pink #gravity swap
            if j.block_type == "full":
                pygame.draw.rect(screen, color, current)
            elif j.block_type == "slope45":
                draw_slope45(j, color)

    screen.blit(font.render("current block: " + str(current_block), True, black), (1050, 20))
    screen.blit(font.render("current type: " + str(current_type), True, black), (1050, 140))
    screen.blit(font.render("current rotation: " + str(current_rotation), True, black), (1050, 200))

    gridtext = ''
    if gridint == 0:
        gridtext = "maplayout"
    elif gridint == 1:
        gridtext = "mapentities"
    elif gridint == 2:
        gridtext = "mapetc"

    screen.blit(font.render("current grid: " + gridtext, True, black), (1020, 260))

    for button in buttons:
        button.draw()

    pygame.display.flip()
