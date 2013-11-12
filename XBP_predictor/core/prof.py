# Decorator for CPU Profiling [Written in Expert Python p.330-331.]

import tempfile
import os
import cProfile
import pstats

def profile(column='time',list=10):
	# list is the number of time of waste by function called.
	def _profile(function):
		def __profile(*args,**kw):
			lv,s = tempfile.mkstemp()
			profiler = cProfile.Profile()
			try:
				return profiler.runcall(function,*args,**kw)
			finally:
				profiler.dump_stats(s)
				p = pstats.Stats(s)
				p.sort_stats(column).print_stats(list)
		return __profile
	return _profile

