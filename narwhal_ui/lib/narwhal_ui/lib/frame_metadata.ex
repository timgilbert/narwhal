defmodule NarwhalUi.FrameMetadata do
  @moduledoc false
  require Logger
  alias Unicorn.Frame

  @type t :: %__MODULE__{name: String.t(), id: String.t(), frame: %Frame{}}
  defstruct [:name, :id, :frame, :created_at, :updated_at]

  def new(id, %{name: name, frame: frame}) do
    now = DateTime.utc_now()
    Logger.debug(inspect frame)
    new_frame = Frame.new(frame)
    %__MODULE__{
      id: id,
      name: name,
      frame: new_frame,
      created_at: now,
      updated_at: now
    }
  end
end
