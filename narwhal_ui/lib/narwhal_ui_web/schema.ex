defmodule NarwhalUiWeb.Schema do
  @moduledoc false

  use Absinthe.Schema

  import_types NarwhalUiWeb.Schema.Color
  import_types NarwhalUiWeb.Schema.Frame
  import_types NarwhalUiWeb.Schema.Timeline
  import_types NarwhalUiWeb.Schema.Effect

  query do
    @desc "Get a random frame"
    field :random_frame, :frame do
      resolve &NarwhalUiWeb.Resolvers.Frame.rand/3
    end

    @desc "List all saved frames"
    field :saved_frames, list_of(:frame) do
      resolve &NarwhalUiWeb.Resolvers.Frame.all_saved_frames/3
    end

    @desc "List all saved timelines"
    field :saved_timelines, list_of(:timeline) do
      resolve &NarwhalUiWeb.Resolvers.Frame.all_saved_frames/3
    end
  end

  mutation do
    @desc "Create a new frame"
    field :create_frame, :create_frame_response do
      arg :input, non_null(:new_frame)
      resolve &NarwhalUiWeb.Resolvers.Frame.create_frame/3
    end
  end
end
