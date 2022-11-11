from datetime import datetime

def WrLog(logstr):
    dt = datetime.now()
    timelog = dt.strftime('%Y-%m-%d %H:%M:%S %f')
    info = timelog + "  " + logstr + "\n"
    with open('../result/log.txt', 'a') as f:
        f.writelines(info)