# narwhal

This is me trying to mess around with Elixir, [Nerves](http://nerves-project.org/), and the 
[Unicorn Hat HD](https://shop.pimoroni.com/products/unicorn-hat-hd) on a raspberry pi.

## TODO

- unicorn.runner should publish to a 
  [Registry](https://hexdocs.pm/elixir/Registry.html#module-using-as-a-pubsub)
  - run command to run over the timeline, generate and publish the 
    next frame, sleep for appropriate time 
- UI: complete timeline editing ui
- UI: start/pause/rewind/step though runner
- UI: web display that reacts to published frames
- FW: GenServer that listens on registry, pushes frames to hat
- Lib: rejigger tween code to not produce intermediate steps
- Lib: implementation should just be a single list
- UI: gql interface to detect connected hardware
  - Live-edit mode for grid if it's there
- General: maybe should think about time in terms of FPS and have
  the runner pass the current time into the timeline (versus pausing 
  for x ms)