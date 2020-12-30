defmodule UnicornTest do
  use ExUnit.Case
  doctest Unicorn

  test "greets the world" do
    assert Unicorn.hello() == :world
  end
end
