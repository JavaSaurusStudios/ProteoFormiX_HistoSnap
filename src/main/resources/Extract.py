import argparse
import random
import sys
from pyimzml.ImzMLParser import ImzMLParser
from statistics import mean

skipSize=100
noiseThreshold=0
calculateBackground=False

parser = argparse.ArgumentParser(description='Extracting MZ subsections.')
parser.add_argument("--mzMin",type=float)
parser.add_argument("--mzMax",type=float)
parser.add_argument("--input")
parser.add_argument("--output")

args=parser.parse_args()

mzMin=args.mzMin
mzMax=args.mzMax
input=args.input
output=args.output

#print("Input file = ",input)
#print("Output file = ",output)
#print("Extracting data between ",mzMin," and ",mzMax," that have an intensity >= ",noiseThreshold)
sys.stdout.flush() 
p = ImzMLParser(input)

if calculateBackground:
 print("Calculating background noise...\n")
 #calculate the background
 background={}
 idx=0
 while idx <len(p.coordinates):
  mzs, intensities = p.getspectrum(idx)
  (x,y,z)=p.coordinates[idx]
  if not x in background.keys():
   background[x]={}
  if not y in background[x].keys():
   background[x][y]=mean(intensities)
  idx+=1
 
print("Extracting spectra...") 
f=open(output,"w")
spectra=0;
#print("Processing ",len(p.coordinates)," spectra...")  
sys.stdout.flush() 
for idx, (x,y,z) in enumerate(p.coordinates):
 mzs, intensities = p.getspectrum(idx)
 line=">"+str(idx)+"\t"+str(x)+"\t"+str(y)+"\t"+str(z)+"\n"
 spectra+=1
 if spectra%1000==0:
  #print("Processed ",str(spectra)," spectra ",100*spectra/len(p.coordinates),"%") 
   print(100*spectra/len(p.coordinates),"%") 
   sys.stdout.flush() 
 
 i=0  
  #Skip to the area of interest in spectra
 while i<len(mzs):
 
  if mzs[i]<=mzMin:
   i+=skipSize 
  else:
   #reverse the skip so we continue from there
   i-=skipSize
   if i<0:
    i=0
   break

 #Analyze the area of interest, break when the upper limiter is reached
 while i<(len(mzs)-1):
  i+=1
  if mzs[i]>=mzMin and mzs[i]<=mzMax:
   if calculateBackground:
    tmp=intensities[i]-background[x][y]
   else: 
    tmp=intensities[i]
   if tmp>=noiseThreshold:
    line+=str(mzs[i])+"\t"+str(tmp)+"\n"
  if i>=len(mzs) or mzs[i]>=mzMax:
   _f=f.write(line)
   break   
 
f.close()

