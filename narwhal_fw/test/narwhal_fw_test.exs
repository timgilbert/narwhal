defmodule NarwhalFwTest do
  use ExUnit.Case
  doctest NarwhalFw

  test "greets the world" do
    assert NarwhalFw.hello() == :world
  end
end
