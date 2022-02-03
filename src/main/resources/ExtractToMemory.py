import argparse
import sys
from pyimzml.ImzMLParser import ImzMLParser

parser = argparse.ArgumentParser(description='Extracting MZ subsections.')
parser.add_argument("--input")
args=parser.parse_args()

input=args.input

print("Input file = ",input)

p = ImzMLParser(input)
noiseThreshold=5

for idx, (x,y,z) in enumerate(p.coordinates):
 mzs, intensities = p.getspectrum(idx)
 print(">"+str(idx)+"\t"+str(x)+"\t"+str(y)+"\t"+str(z))
 sys.stdout.flush() 
 i=0
 #Analyze the area of interest, break when the upper limiter is reached
 while i<(len(mzs)-1):
  i+=1
  if intensities[i]>=noiseThreshold:
   print(str(mzs[i])+"\t"+str(intensities[i]))
   sys.stdout.flush() 

