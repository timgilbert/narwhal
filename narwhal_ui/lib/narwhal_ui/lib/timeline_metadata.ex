defmodule NarwhalUi.TimelineMetadata do
  @moduledoc false
  require Logger
  alias Unicorn.Timeline

  @type t :: %__MODULE__{
               name: String.t(),
               id: String.t(),
               timeline: %Timeline{}
             }
  defstruct [:name, :id, :timeline, :created_at, :updated_at]

#  def steps(%{steps: steps}) do
#    steps
#    |> Enum.map(fn %{pause_ms: pause_ms} -> Step.new() end)
#  end

  def new(id, %{name: name, timeline: timeline}) do
    now = DateTime.utc_now()
    Logger.debug(inspect timeline)
    new_timeline = Timeline.new(timeline)
    %__MODULE__{
      id: id,
      name: name,
      timeline: new_timeline,
      created_at: now,
      updated_at: now
    }
  end
end
