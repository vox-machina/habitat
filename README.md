# Habitat

## About

Tracks file systems you care about on your different machines generating events to send into the Nanonics architecture to track events you are interested in over time.

Perhaps this will help you remember what you were working on?

## Usage

Requires:
- Clojure 1.11.1 or newer
- Babashka 1.3.188

```bash
$ bb -m voxmachina.habitat --src /home/your-home-dir/src
```

Will track changes to the 'src' directory in your home directory.
