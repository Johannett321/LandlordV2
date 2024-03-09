import numpy as np
import matplotlib.pyplot as plt
import random
import csv

# 0.0008 equals 7 times increase over 4 days. 0.0005 equals 4 times over 4 days. 0.00005 equals 1.3 times increase over 4 days. Therefore range is: 0.00005-0.0008
min_positive_increase =  0.00005
max_positive_increase = 0.0004

def biased_random_walk(bias_factor, steps=100):
    # Start with the initial value of 1
    current_value = 1.0
    values = [current_value]  # List to store the walk values

    # Random step, influenced by the bias
    scale = bias_factor * 75
    if (bias_factor < 0):
        scale = 0.004
        print("Negative number!")


    for _ in range(steps):
        step = np.random.normal(loc=bias_factor, scale=scale)  # Adjust scale for variability # 0.002 is best for negative
        if current_value <= 0:
            current_value = 0
            values.append(0)
            continue
        current_value += step
        values.append(current_value)

    return values

def generateCsv(values, csv_file_path):
    with open(csv_file_path, mode='w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(values)  # Writing all values in one row
        #for value in values:
                #writer.writerow([value])  # Writing each value in a new row

    # Read the values from the file
    with open(csv_file_path, 'r') as file:
        line = file.readline().strip()
        values = list(map(float, line.split(',')))

def plotFigure(values, csv_file_path, plot_file_path):
    reduced = values[::1000]
    # Plot the values
    plt.plot(values, marker='o', linewidth=1, markersize=0.3)  # Using 'o' as marker for each point
    plt.title('Values from File')
    plt.xlabel('Index')
    plt.ylabel('Value')
    plt.grid(True)

    # Save the plot to a PNG file
    plt.savefig(plot_file_path, dpi=300)  # dpi specifies the resolution

    # Optionally, you can clear the current figure to free memory if needed
    plt.clf()

def calculateRandomBias():
    random_bias = random.uniform(min_positive_increase, max_positive_increase) #positive
    #random_bias = -0.00001 # Range -0.00001 to -0.00023 is good for negative #negative

    return random_bias




def main():
    csv_file_path = "./biased_random_walk.csv"
    output_png_file = './plot.png'

    print ("Generating bias...")
    random_bias = calculateRandomBias()

    print ("Doing random walk...")
    values = biased_random_walk(random_bias, 483840) # 5760 = 4 days, 20160 = 14 days, 40320 = 1 month. 483840 = 1 year

    print ("Creating CSV...")
    generateCsv(values, csv_file_path)

    print ("Plotting figure...")
    plotFigure(values, csv_file_path, output_png_file)

main()
