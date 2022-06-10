import json
import random
import sys

from deap import tools

j = json.loads(sys.argv[1])

D = j['matrix']

startV = j['startVertex']
endV = j['targetVertex']
LENGTH_D = len(D)

POPULATION_SIZE = j['populationSize']
P_CROSSOVER = j['crossover']
P_MUTATION = j['mutation']
MAX_GENERATIONS = j['generations']


def fitness(path):
    s = 0.0
    if path[0] != startV:
        if D[startV][path[0]] <= 0.0:
            s += 10 ** len(path)
        else:
            s += D[startV][path[0]]
        if path[0] == endV:
            return s
    for i in range(0, len(path) - 1):
        dist = D[path[i]][path[i + 1]]
        if dist <= 0.0:
            s += 10 ** (len(path) - i)
        else:
            s += dist
        if path[i + 1] == endV:
            break
    return s


def getBestPath(paths):
    bestInd = 0
    bestValue = fitness(paths[0])
    for i in range(1, len(paths)):
        if fitness(paths[i]) <= bestValue:
            bestValue = fitness(paths[i])
            bestInd = i
    return paths[bestInd]


population = [random.sample(range(LENGTH_D), LENGTH_D) for x in range(POPULATION_SIZE)]




for iteration in range(MAX_GENERATIONS):
    newGeneration = []
    random.shuffle(population)
    for i in range(0, POPULATION_SIZE, 2):
        ind1 = population[i].copy()
        ind2 = population[i + 1].copy()
        if random.random() <= P_CROSSOVER:
            ind1, ind2 = tools.cxOrdered(ind1, ind2)

        if random.random() <= P_MUTATION:
            ind1 = tools.mutShuffleIndexes(ind1, 1)[0]
        if random.random() <= P_MUTATION:
            ind2 = tools.mutShuffleIndexes(ind2, 1)[0]
        if fitness(ind1) < fitness(population[i]):
            newGeneration.append(ind1)
        else:
            newGeneration.append(population[i])
        if fitness(ind2) < fitness(population[i + 1]):
            newGeneration.append(ind2)
        else:
            newGeneration.append(population[i + 1])
    population = newGeneration

print(getBestPath(population))
