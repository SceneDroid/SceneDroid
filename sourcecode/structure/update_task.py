class myupdate:
    def __init__(self, project_1, project_2, work_dir):
        """
        :param project_1: 较旧版本号的项目对象
        :param project_2: 较新版本号的项目对象
        :param work_dir: 这轮工作的目录
        """
        self.p1 = project_1
        self.p2 = project_2
        self.work_dir = work_dir
        print("=========================================")
        print("[+] init update work object : ", work_dir)
        print("=========================================")
