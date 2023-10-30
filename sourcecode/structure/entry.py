class myentry:
    def __init__(self, activity, vector, startadb, widgets):
        self.activity = activity
        self.vector = vector
        self.startadb = startadb
        self.widgets = widgets

    def putself(self):
        print("[vector]: ", self.vector)
        print("[activity]: ", self.activity)
        print("[startadb]: ", self.startadb)
        index = 0
        for widget in self.widgets:
            print(str(index), widget)
            index = index + 1