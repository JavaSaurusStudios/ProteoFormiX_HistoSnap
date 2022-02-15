import sqlite3
import argparse
import random
import datetime;
import sys
from pyimzml.ImzMLParser import ImzMLParser
from statistics import mean
from sqlite3 import Error

sql_create_table = """CREATE TABLE IF NOT EXISTS pixels (
                                        rank INTEGER PRIMARY KEY AUTOINCREMENT,
                                        x INTEGER NOT NULL,
                                        y INTEGER NOT NULL,
                                        mz REAL NOT NULL, 
                                        i REAL NOT NULL
                                    ); """

#                                        PRIMARY KEY (x,y,mz)

#sql_create_index= """CREATE INDEX index_x ON pixels(x)"""
#sql_create_index_2="""CREATE INDEX index_y ON pixels(y)"""

sql_create_index_3="""CREATE INDEX index_mz ON pixels(mz)"""

sql_create_entry=  """ INSERT INTO pixels(x,y,mz,i) VALUES(?,?,?,?) """

parser = argparse.ArgumentParser(description='Extracting MZ subsections.')
parser.add_argument("--input")
args=parser.parse_args()
input=args.input

threshold=8
batchsize=1000

print("Input file = ",input)

db_file=input+".db"
print("Creating Database Connection at ",db_file)
conn=None
try:
 conn=sqlite3.connect(db_file)
 conn.isolation_level=None
 c = conn.cursor()
 c.execute(sql_create_table)
 print("set PRAGMA mode")
 
 c.execute("PRAGMA journal_mode = OFF;")
 c.execute("PRAGMA synchronous = OFF;")
 c.execute("PRAGMA cache_size = 1000000;")
 c.execute("PRAGMA locking_mode = EXCLUSIVE;")
 c.execute("PRAGMA temp_store = MEMORY;")
 print("Creating indices")
 #c.execute(sql_create_index)
 #c.execute(sql_create_index_2)
 #c.execute(sql_create_index_3)
 sys.stdout.flush() 
 p = ImzMLParser(input)

 print("Extracting spectra...") 
 
 spectra=0

 print("Processing ",len(p.coordinates)," spectra...")  
 sys.stdout.flush() 
 data=[]
 for idx, (x,y,z) in enumerate(p.coordinates):
  mzs, intensities = p.getspectrum(idx)
  spectra+=1

  if spectra%batchsize==0:
   print("Storing batch...")   
   ct = datetime.datetime.now()
   print("current time:-", ct)
   c.execute('begin')   
   c.executemany(sql_create_entry,data)
  # conn.commit()
   c.execute('commit')
   data=[]
   ct = datetime.datetime.now()
   print("current time:-", ct)
   print("Processed ",str(spectra)," spectra ",100*spectra/len(p.coordinates),"%") 
   sys.stdout.flush() 
  

  i=0  
 #Analyze the area of interest, break when the upper limiter is reached
  while i<(len(mzs)-1):
   i+=1
   mzValue=float(mzs[i])
   if(mzValue>=threshold):
    data.append((x,y, mzValue, float(intensities[i])))  
   
 if len(data)>0:
  ct = datetime.datetime.now()
  print("current time:-", ct)
  c.execute('begin')
  c.executemany(sql_create_entry,data)
 # conn.commit()
  c.execute('commit')
  data=[]
  ct = datetime.datetime.now()
  print("current time:-", ct)

 print("Done ")
except Error as e:
 print(e)
finally:
 if conn:
  conn.close()