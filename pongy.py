import math
import pygame
import random

BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
BLUE = (0, 0, 255)
GREEN = (0, 255, 0)
YELLOW = (255, 255, 0)
RED = (255, 0, 0)

COMPUTER_MAX_VELOCITY = 3
LEVEL_UP_COUNT = 5

screen = None
font = None

class Shape():

    screenWidth = None
    screenHeight = None

    xCoord = None
    yCoord = None

    def getX(self):
        return self.xCoord

    def getY(self):
        return self.yCoord

    def getScreenWidth(self):
        return self.screen_width

    def getScreenHeight(self):
        return self.screen_height

    def setX(self, xCoordIn):
        self.xCoord = xCoordIn

    def setY(self, yCoordIn):
        self.yCoord = yCoordIn

    def setScreenWidth(self, screenWidthIn):
        self.screenWidth = screenWidthIn

    def setScreenHeight(self, screenHeightIn):
        self.screenHeight = screenHeightIn

    def calculateCoords(self):
        pass

class Paddle(Shape):

    paddleWidth = 100
    paddleHeight = 8
    score = 0

    def calculateCoords(self):
        if (self.yCoord - (self.paddleWidth / 2)) <= 1:
            self.yCoord = (self.paddleWidth / 2)
        if self.yCoord > self.screenHeight - (self.paddleWidth / 2):
            self.yCoord = self.screenHeight - (self.paddleWidth / 2)

    def incrementScore(self):
        self.score += 1

    def getScore(self):
        return self.score

    def getWidth(self):
        return self.paddleWidth

    def getHeight(self):
        return self.paddleHeight

    def setWidth(self, paddleWidthIn):
        self.paddleWidth = paddleWidthIn

    def setHeight(self, paddleHeightIn):
        self.paddleHeight = paddleHeightIn

    def getRect(self):
        return pygame.Rect(
            self.xCoord - (self.paddleHeight / 2),
            self.yCoord - (self.paddleWidth / 2),
            self.paddleHeight,
            self.paddleWidth
        )

class Ball(Shape):

    ballRadius = 10
    angle = 0
    velocity = 3

    def calculateCoords(self):
        if (self.yCoord - self.ballRadius) < 0:
            self.yCoord = 0 + self.ballRadius
            self.angle = (360 - self.angle) % 360
        elif (self.yCoord + self.ballRadius) > self.screenHeight:
            self.yCoord = self.screenHeight - self.ballRadius
            self.angle = (360 - self.angle) % 360
        self.xCoord += self.velocity * math.cos(self.angle * math.pi / 180)
        self.yCoord += self.velocity * math.sin(self.angle * math.pi / 180)

    def resetGame(self):
        self.setX(self.screenWidth / 2)
        self.setY(self.screenHeight / 2)
        self.resetVelocity()

    def incrementVelocity(self):
        self.velocity += 1

    def resetVelocity(self):
        self.velocity = 3

    def getRadius(self):
        return self.ballRadius

    def getAngle(self):
        return self.angle

    def getVelocity(self):
        return self.velocity

    def getScreenWidth(self):
        return self.screenWidth

    def getScreenHeight(self):
        return self.screenHeight

    def setRadius(self, ballRadiusIn):
        self.ballRadius = ballRadiusIn

    def setAngle(self, angleIn):
        self.angle = angleIn

    def setVelocity(self, velocityIn):
        self.velocity = velocityIn

    def getRect(self):
        return pygame.Rect(
            self.xCoord - self.ballRadius,
            self.yCoord - self.ballRadius,
            2 * self.ballRadius,
            2 * self.ballRadius
        )

if __name__ == '__main__':

    pygame.init()
    screen = pygame.display.set_mode((0, 0), pygame.FULLSCREEN)
    font = pygame.font.Font(None, 48)
    ball = Ball()

    player1Paddle = Paddle()
    player2Paddle = Paddle()

    ball.setX(screen.get_width() / 2)
    ball.setY(screen.get_height() / 2)
    ball.setScreenWidth(screen.get_width())
    ball.setScreenHeight(screen.get_height())
    ball.setAngle(0)

    player1Paddle.setX(50)
    player1Paddle.setY(screen.get_height() / 2)
    player1Paddle.setScreenWidth(screen.get_width())
    player1Paddle.setScreenHeight(screen.get_height())

    player2Paddle.setX(screen.get_width() - 50)
    player2Paddle.setY(screen.get_height() / 2)
    player2Paddle.setScreenWidth(screen.get_width())
    player2Paddle.setScreenHeight(screen.get_height())

    hitCount = 0
    exitGame = False

    while True:

        if exitGame:
            break

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                exitGame = True
            elif (
                event.type == pygame.KEYDOWN and
                event.key == pygame.K_ESCAPE
            ):
                exitGame = True

        ball.calculateCoords()

        if 90 <= ball.getAngle() and ball.getAngle() <= 270:
            if (
                (player1Paddle.getY() - ball.getY()) >
                COMPUTER_MAX_VELOCITY
            ):
                player1Paddle.setY(
                    player1Paddle.getY() - COMPUTER_MAX_VELOCITY
                )
            elif ((ball.getY() - player1Paddle.getY()) >
                COMPUTER_MAX_VELOCITY
            ):
                player1Paddle.setY(
                    player1Paddle.getY() + COMPUTER_MAX_VELOCITY
                )
            else:
                player1Paddle.setY(ball.getY())
        player1Paddle.calculateCoords()
        
        player2Paddle.setY(pygame.mouse.get_pos()[1])
        player2Paddle.calculateCoords()

        if ball.getX() <= 0:
            player2Paddle.incrementScore()
            ball.resetGame()
            ball.setAngle(180)
            hitCount = 0
        elif ball.getX() >= screen.get_width():
            player1Paddle.incrementScore()
            ball.resetGame()
            ball.setAngle(0)
            hitCount = 0
            
        scoreText = font.render(
            '{} - {}'.format(
                str(player1Paddle.getScore()),
                str(player2Paddle.getScore())
            ),
            False,
            WHITE
        )

        if player1Paddle.getRect().colliderect(ball.getRect()):
            #ball.setAngle(
            #    round((
            #            (ball.getY() - player1Paddle.getY()) /
            #            player1Paddle.getWidth()
            #        ) *
            #        120
            #    )
            #)
            ball.setAngle(random.randrange(-60, 60))
            hitCount += 1
        
            scoreText = font.render(
                str(ball.getAngle()),
                False,
                WHITE
            )
            if (hitCount % LEVEL_UP_COUNT) <= 0:
                ball.incrementVelocity()
        if player2Paddle.getRect().colliderect(ball.getRect()):
            ball.setAngle(
                180 - (
                    (ball.getY() - player2Paddle.getY()) /
                    player2Paddle.getWidth()
                ) *
                120
            )
            hitCount += 1
            if (hitCount % LEVEL_UP_COUNT) <= 0:
                ball.incrementVelocity()
            
        screen.fill(BLACK)
        pygame.draw.ellipse(
            screen,
            WHITE,
            ball.getRect(),
            1
        )
        pygame.draw.rect(
            screen,
            WHITE,
            player1Paddle.getRect(),
            1
        )
        pygame.draw.rect(
            screen,
            WHITE,
            player2Paddle.getRect(),
            1
        )

        screen.blit(
            scoreText, (
                int(screen.get_width() / 2),
                int(scoreText.get_height() / 2)
            )
        )
        pygame.display.flip()

    pygame.quit()
