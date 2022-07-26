import argparse
import sys
import bisect
from pyimzml.ImzMLParser import ImzMLParser

noiseThreshold=0

parser = argparse.ArgumentParser(description='Extracting MZ subsections.')
parser.add_argument("--mzMin")
parser.add_argument("--mzMax")
parser.add_argument("--threshold",type=float)
parser.add_argument("--input")
parser.add_argument("--output")

args=parser.parse_args()

noiseThreshold=args.threshold
#A list of mz range starts
mzMin_list = []
for item in args.mzMin.split():
  mzMin_list.append(float(item))
#A list of mz range ends
mzMax_list = []
for item in args.mzMax.split():
  mzMax_list.append(float(item))

input=args.input
output=args.output

p = ImzMLParser(input)
f=open(output,"a")
spectra=0

for idx, (x,y,z) in enumerate(p.coordinates):
 #EXPORT THE PROGRESS
 spectra+=1
 if spectra%1000==0:
   print(100*spectra/len(p.coordinates),"%") 
   sys.stdout.flush() 
 mzs, intensities = p.getspectrum(idx)
 line=">"+str(idx)+"\t"+str(x)+"\t"+str(y)+"\t"+str(z)+"\n"

 rangeIndex=0  
 mzMin=mzMin_list[0]
 mzMax=mzMax_list[0]
 #mzIndex = next(x[0] for x in enumerate(mzs) if x[1] > mzMin)-1
 mzIndex=bisect.bisect_left(mzs, mzMin)
 if mzIndex<0:
  mzIndex=0

 while mzIndex<(len(mzs)-1):
  mzIndex+=1
  ##MOVE TO THE NEXT RANGE
  mzValue=mzs[mzIndex]
  
  if mzValue>=mzMin:
   ##CHECK IF THE MZVALUE FITS THE RANGE
   if mzValue>=mzMin and mzValue<=mzMax:
    tmp=intensities[mzIndex]
    if tmp>=noiseThreshold:
     line+=str(mzValue)+"\t"+str(tmp)+"\n"
  
   if mzValue>=mzMax:
    _f=f.write(line)

   if mzValue>mzMax:
      ##GET THE NEXT RANGE
    rangeIndex+=1
    if rangeIndex>=len(mzMin_list):
     _f=f.write(line)
     break

    mzMin=mzMin_list[rangeIndex]
    mzMax=mzMax_list[rangeIndex]
    #mzIndex = next(x[0] for x in enumerate(mzs) if x[1] > mzMin)-1
    mzIndex=bisect.bisect_left(mzs, mzMin)

f.close()

