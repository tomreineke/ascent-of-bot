import argparse
import asyncio
from pathlib import Path

print("Running Gimp...")

gimp_location = """../tools/GIMP 2/bin/gimp-2.10.exe"""

async def run_gimp(script_path, args):
    escaped_path = repr(str(script_path.absolute().parent))
    stem = script_path.stem
    arg_array = repr(args)
    proc = await asyncio.create_subprocess_exec(
        ##[
        gimp_location,
        "-i",
        "--batch-interpreter",
        "python-fu-eval",
        "-b",
        f"""
import sys;
sys.path.append({escaped_path})
import {stem}
{stem}.main({arg_array})
        """,
        "-b",
        "pdb.gimp_quit(1)",
        ##],
        stdin=asyncio.subprocess.PIPE,
        stdout=asyncio.subprocess.PIPE,
        stderr=asyncio.subprocess.PIPE
    )
    stderr = proc.stderr
    stdin = proc.stdin
    has_error = False
    remaining_waits = 10
    while not proc.returncode and remaining_waits > 0:
        line = None
        try:
            line = await asyncio.wait_for(stderr.readline(), timeout=0.5)
            if line == b'':
                break
        except asyncio.TimeoutError:
            if has_error:
                remaining_waits -= 1
            continue
        if line == b'batch command executed successfully\n':
            remaining_waits = 0
        if line == b'batch command experienced an execution error\n':
            has_error = True
            remaining_lines = 20
        print(line.decode("utf-8"), end="")
    proc.kill()
    await proc.wait()
    
parser = argparse.ArgumentParser(description="Run Gimp.")
parser.add_argument("script_file", metavar="script_file", help="the Python script file to run")
parser.add_argument("args", metavar="args", nargs="*", help="arguments passed to the script")
args = parser.parse_args()

asyncio.run(run_gimp(Path(args.script_file), args.args))

print("Finished running Gimp.")