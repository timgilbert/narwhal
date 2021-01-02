defmodule Unicorn.Fx.Random do
  alias Unicorn.Frame

  defimpl Unicorn.Fx.Effect, for: Random do
    @spec call(any, Frame.t()) :: Frame.t()
    def call(_c, _f), do: Frame.rand()
  end
end
