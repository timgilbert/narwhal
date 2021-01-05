defmodule Unicorn.Fx do
  alias Unicorn.Frame

  defprotocol Effect do
    @spec call(effect :: struct, frame :: Frame.t()) :: Frame.t()
    def call(effect, frame)
  end
end
