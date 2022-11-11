class screen:
    def __init__(self, vector, sadb, act, stype=False):
        """
        :param xml: 对应的布局XML文件
        :param vector: 对应的特征向量
        :param typeAct: 是否为根Activity
        :param command: adb操作路径信息
        :param parentScreen： 父Screen节点ID
        :param act： 所属的Activity
        """
        self.fact = ""
        self.nextact = []
        self.act = act
        self.vector = vector
        self.sonScreen = []
        # 是否为根Activity
        self.stype = stype  # True or False
        self.adb = sadb
        self.rescommand = []
        self.actrans = []
        self.newfrag = False
        self.fragment = ""
        self.nextfragment = ""

    def printAll(self):
        print("============ screen object ============")
        print("[vector] : ", self.vector)
        print("[parentScreen] : ", self.fact)
        print("[Activity] : ", self.act)
        print("[adb] : ", self.adb)
        print("[fragment] : ", self.fragment)
        print("[next-ragment] : ", self.nextfragment)
        print("[newfrag] : ", self.newfrag)
        print("[rescommand] : ")
        try:
            for widegt in self.rescommand:
                print(widegt.info)
        except:
            pass
        print("=======================================")

