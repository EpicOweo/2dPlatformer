import pygame
import sys, os


pygame.init()

size = width, height = 800, 800
screen = pygame.display.set_mode(size)
scale = 1

white = pygame.Color(255,255,255)
black = pygame.Color(0,0,0)
red = pygame.Color(255, 0, 0)
gray = pygame.Color(128, 128, 128)
brown = pygame.Color(165, 42, 42)
green = pygame.Color(0, 255, 0)


font = pygame.font.Font(None, 50)

grid_width = 50


current_block = 1
max_block = 5


grid = []
for i in range(grid_width):
    grid.append([])
    for j in range(grid_width):
        grid[i].append(0)


grid_size = (height - 300) * scale

grid_thickness = 1
grid_lines = pygame.Rect((width-grid_size-len(grid)) / 2 - grid_thickness, (height-grid_size-len(grid)) / 2 - grid_thickness,
            grid_size + len(grid)-1, grid_size + len(grid)-1)

grid_rects = []

square_size = round(grid_size / len(grid[0])) * scale

class Square:
    def __init__(self, rect, position):
        self.rect = rect
        self.x, self.y = position
        self.block = 0
        self.pressed = False

    def check(self):
        if self.rect.collidepoint(pygame.mouse.get_pos()):
            if pygame.mouse.get_pressed()[0] == True:
                return True

row = 0
for i in grid:
    col = 0
    grid_rects.append([])

    for j in i:
        pt = (width-grid_size-len(grid)) / 2 + (col-1) * grid_thickness + col*square_size, (height-grid_size-len(grid)) / 2 + (row-1) * grid_thickness + row * square_size
        grid_rects[row].append(Square(pygame.Rect(pt, (square_size, square_size)), (row, col)))
        col += 1

    row += 1

last_squares = []

def save_grid():
    i = 0
    while os.path.exists('./map_%s.txt' % i):
        i += 1
    f = open('./map_%s.txt' % i, 'w')
    for i in grid_rects:
        line = ''
        for j in i:
            line += str(j.block)
        f.write(line + '\n')
    f.close()

def change_current_block():
    global current_block
    current_block += 1
    if current_block > max_block:
        current_block = 1

# main loop
while True:

    for event in pygame.event.get():
        if event.type == pygame.QUIT: sys.exit()
        elif event.type == pygame.MOUSEBUTTONDOWN:
            for i in grid_rects:
                for j in i:
                    if j.check() and j not in last_squares:
                        j.pressed = not j.pressed
                        if j.pressed:
                            j.block = current_block
                        else:
                            j.block = 0
                        last_squares.append(j)
                        last_pressed = j.pressed
                        last_orientation = 0
        elif event.type == pygame.KEYDOWN:
            keyspressed = pygame.key.get_pressed()
            if keyspressed[pygame.K_s]:
                save_grid()
            if keyspressed[pygame.K_UP]:
                change_current_block()


    if pygame.mouse.get_pressed()[0] == False:
        last_squares = []
        last_pressed = None
        last_orientation = None # 0: none, 1: vertical, 2: horizontal


    pygame.event.get()

    screen.fill(black)

    pygame.draw.rect(screen, gray, grid_lines)

    for i in grid_rects:
        for j in i:
            current = j.rect


            if j.check() and j not in last_squares and j.pressed != last_pressed and len(last_squares) > 0:
                if last_squares[-1].y != j.y:
                    orientation = 1
                elif last_squares[-1].x != j.x:
                    orientation = 2
                if last_squares[-1].y != j.y and last_squares[-1].x != j.x:
                    orientation = None

                if orientation == last_orientation or last_orientation == 0:
                    j.pressed = not j.pressed
                    last_squares.append(j)
                    last_orientation = orientation

            color = white

            if j.pressed:
                if j.block == 0: j.block = current_block
                if j.block == 1: color = gray
                elif j.block == 2: color = green
                elif j.block == 3: color = brown
                elif j.block == 4 or j.block == 5: j.block = 0

            pygame.draw.rect(screen, color, current)
            screen.blit(font.render('current block: ' + str(current_block), True, white), (10, 10))

    pygame.display.flip()
