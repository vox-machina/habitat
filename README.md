# Habitat

## About

Tracks file systems you care about on your different machines generating events to send into the Nanonics architecture to track events you are interested in over time.

Perhaps this will help you remember what you were working on?

As this is Babashka not Clojure the Babashka default log setup is used (in this case a Timbre impl).

## Usage

Requires:
- Clojure 1.11.1 or newer
- Babashka 1.3.188

#### Run habitat watching a location on the filesystem

```bash
$ bb -m voxmachina.habitat --src /home/your-home-dir/src
```

Will track changes to the 'src' directory in your home directory.

#### Release a version of habitat

This will create or update the version.edn file which is not in version control but should be packaged as part of a release.

```bash
$ bb -x build/release
```
