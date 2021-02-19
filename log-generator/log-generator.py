import linecache
from datetime import datetime
import random, time, sys


def print_help_usage():
    print(f'Usage: {sys.argv[0]} <n_output_lines_per_minute>')

if __name__ == '__main__':
    # Need to know how many words are there in the dictionanry
    n_words_available = sum(1 for line in open('words.txt'))
    # Parse cli arguments
    lines_per_min = 1
    if len(sys.argv) > 1:
        if sys.argv[1] == '--help':
            print_help_usage()
            exit(0)
        try:
            lines_per_min = float(sys.argv[1])
        except ValueError:
            print_help_usage()
            exit(1)

    interval_sec = 60/lines_per_min

    log_levels = ['INFO', 'WARNING', 'ERROR']
    n_log_levels = len(log_levels)

    while True:
        n_words_for_this_line = random.randint(1, 15)
        log_level = log_levels[random.randrange(0, n_log_levels)]
        now = datetime.now()
        log_line = f"{now.strftime('%Y-%m-%d %H:%M:%S,%f')[:-3]} {log_level}"
        for i in range(0, n_words_for_this_line):
            # pick a random word and add it to the log line
            new_word = linecache.getline('words.txt',random.randint(0, n_words_available)).strip('\n')
            log_line = f"{log_line} {new_word}"

        print(log_line)  # TODO: SUBMIT THE LOG
        time.sleep(interval_sec)