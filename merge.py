import os, sys


def getjobs(log_files):
    jobs = set()
    for i in range(len(log_files)):
        for j, let in enumerate(log_files[i]):
            if let.isdigit():
                if len(log_files[i][1:j-1]) != 0:
                    jobs.add(log_files[i][1:j-1])
                    break
    return list(jobs)

def main():

    tweetsdir = sys.argv[1]
    outputdir = sys.argv[2]
    
    #get all of the jobs
    jobs = getjobs(os.listdir(tweetsdir))

    for job in jobs:
        #filename = sys.argv[1]
        files = os.popen('ls ' + tweetsdir + '/*' + job + '*').read()
        #files = os.system('ls *' + filename + '*')

        print (files)

        outfile = open(outputdir + "/" + job + ".tsv", 'a')

        for f in files.split():
            print "File:", f
        
            tweetfile = open(f, 'r')
            for thing in tweetfile:
                outfile.write(thing)
        
        outfile.close()
        os.system('rm ' + tweetsdir + '/_' + job + '*.txt*')


main()
