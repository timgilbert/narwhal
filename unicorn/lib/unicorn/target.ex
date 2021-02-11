defmodule Unicorn.Target do
  alias Unicorn.Color

  defprotocol FrameTarget do
    # Hmm
  end

  defmodule Random do
    @type t :: %__MODULE__{}
    defstruct []

    @spec new() :: t()
    def new() do
      %__MODULE__{}
    end
  end

  defmodule Solid do
    @type t :: %__MODULE__{color: Color.t()}
    defstruct [:color]

    @spec new(Color.t()) :: t()
    def new(color) do
      %__MODULE__{color: color}
    end
  end

  defmodule Saved do
    @type t :: %__MODULE__{frame_id: String.t()}
    defstruct [:frame_id]

    @spec new(String.t()) :: t()
    def new(frame_id) do
      %__MODULE__{frame_id: frame_id}
    end
  end

end
