defmodule NarwhalUiWeb.Schema do
  @moduledoc false

  use Absinthe.Schema

  import_types NarwhalUiWeb.Schema.Color
  import_types NarwhalUiWeb.Schema.Frame
  import_types NarwhalUiWeb.Schema.General
  import_types NarwhalUiWeb.Schema.Timeline
  import_types NarwhalUiWeb.Schema.Effect

  query do
    @desc "Get a random frame"
    field :random_frame, non_null(:frame) do
      resolve &NarwhalUiWeb.Resolvers.Frame.rand/3
    end

    @desc "Get a solid-color frame"
    field :solid_frame, non_null(:frame) do
      arg :color, :rgb_color, description: "The color to set the frame to (default black)"
      resolve &NarwhalUiWeb.Resolvers.Frame.solid/3
    end

    @desc "List all saved frames"
    field :all_frames, non_null(list_of(:frame_metadata)) do
      arg :options, :sort_options, description: "Sort options for the frames"
      resolve &NarwhalUiWeb.Resolvers.Frame.all_saved_frames/3
    end

    @desc "Get a single frame by its ID"
    field :frame, :frame_metadata do
      arg :id, :string, description: "Sort options for the frames"
      resolve &NarwhalUiWeb.Resolvers.Frame.frame_by_id/3
    end

    @desc "List all saved timelines"
    field :all_timelines, list_of(:timeline_metadata) do
      resolve &NarwhalUiWeb.Resolvers.Timeline.all_saved_timelines/3
    end

    @desc "Return an empty timeline"
    field :empty_timeline, :empty_timeline_response do
      resolve &NarwhalUiWeb.Resolvers.Timeline.empty_timeline/3
    end
  end

  mutation do
    @desc "Create a new named frame"
    field :create_frame, :create_frame_response do
      arg :input, non_null(:new_frame_metadata)
      resolve &NarwhalUiWeb.Resolvers.Frame.create_frame/3
    end

    @desc "Delete a frame"
    field :delete_frame, :delete_frame_response do
      arg :input, non_null(:deleted_frame_request)
      resolve &NarwhalUiWeb.Resolvers.Frame.delete_frame/3
    end

    @desc "Delete a frame"
    field :update_frame, :update_frame_response do
      arg :input, non_null(:update_frame_request)
      resolve &NarwhalUiWeb.Resolvers.Frame.update_frame/3
    end
  end
end
