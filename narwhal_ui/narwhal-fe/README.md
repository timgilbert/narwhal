# narwhal-fe

Front-end for narwhal.

## Development

To get an interactive development environment run:

    clojure -A:fig:build

This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    rm -rf target/public

To create a production build run:

	rm -rf target/public
	clojure -A:fig:min

## TODO

- Custom cursors
- Color picker, better palette
- Grid customization:
  - No gaps (for thumbnails)
  - rasterize and cache?
    - Maybe [this?](https://github.com/pmkroeker/rasterize-svg/blob/master/src/rasterize.ts])
- Effect chooser:
  - Add effect type (tween/replace)
  - Pause/duration/repeat
  - Dehydrate from local effect model to gql mutation payload
