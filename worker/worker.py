import thread
import Queue


def link_worker(source, destination, caller, event_driven_link=False):
    
    assert ((caller is source) or (caller is destination))
    
    if event_driven_link:
        destination.fast_list.append(source)
    
    if caller is source:
        source.out_list.append(destination)
    elif caller is destination:
        destination.in_list.append(source)

        
class Worker(object):
    
    def __init__(self):
        self.mission_in = Queue.PriorityQueue()
        
        # only those worker who work faster then self
        # shall be maintained in this list
        self.in_list   = []
        self.out_list  = []
        self.fast_list = []
        self.routines  = [self.routine]

        
    @staticmethod
    def infinite_loop(func):
        while True:
            func()
    
    def start_loop(self, new_thread=False):
        if new_thread:
            for routine in self.routines:
                thread.start_new_thread(Worker.infinite_loop, (routine,))
        else:
            Worker.infinite_loop(routine)

    # Do something actively
    def routine(self, ask_todo=True):
        if ask_todo:
            for colleague in self.in_list:
                missions = colleague._export_todo(self)
                for mission in missions:
                    self.add_todo(mission, colleague)
                
        self._routine()
        
        for colleague in self.out_list:
            missions = self._export_todo(colleague)
            for mission in missions:
                colleague.add_todo(mission, self)
    
    # Passively respond to co-workers
    def add_todo(self, mission, assigner):
        """Assign jobs to self by slow up-stream co-workers"""
        if mission != {}:
            self.mission_in.put(mission)
        else:
            return
        
        if assigner in self.fast_list:
            self.routine(ask_todo=False)
    
    # common job to do no matter yo are fast or slow
    def _export_todo(self, receiver):
        """Tell receiver what to do next"""
        raise NotImplementedError("Please Implement " + self.__class__.__name__ + "._export_todo()")

    def _routine(self):
        """Pop and execute commands queued in self.mission_in"""
        raise NotImplementedError("Please Implement " + self.__class__.__name__ + ".routine()")
        