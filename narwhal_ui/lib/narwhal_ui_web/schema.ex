defmodule NarwhalUiWeb.Schema do
  @moduledoc false

  use Absinthe.Schema

  import_types NarwhalUiWeb.Schema.Color
  import_types NarwhalUiWeb.Schema.Frame
  import_types NarwhalUiWeb.Schema.General
  import_types NarwhalUiWeb.Schema.Step
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
      resolve &NarwhalUiWeb.Resolvers.Frame.all_frames/3
    end

    @desc "Get a single frame by its ID"
    field :frame, :frame_metadata do
      arg :id, :string, description: "The ID of the frame to return"
      resolve &NarwhalUiWeb.Resolvers.Frame.frame_by_id/3
    end

    @desc "Get a single timeline by its ID"
    field :timeline, :timeline_metadata do
      arg :id, :string, description: "The ID of the frame to return"
      resolve &NarwhalUiWeb.Resolvers.Timeline.timeline_by_id/3
    end

    @desc "List all saved timelines"
    field :all_timelines, list_of(:timeline_metadata) do
      resolve &NarwhalUiWeb.Resolvers.Timeline.all_timelines/3
    end

    @desc "Return an empty timeline"
    field :empty_timeline, non_null(:timeline) do
      resolve &NarwhalUiWeb.Resolvers.Timeline.empty_timeline/3
    end
  end

  mutation do
    # Frame stuff
    @desc "Create a new frame"
    field :create_frame, :create_frame_response do
      arg :input, non_null(:new_frame_metadata)
      resolve &NarwhalUiWeb.Resolvers.Frame.create_frame/3
    end

    @desc "Delete a frame"
    field :delete_frame, :delete_frame_response do
      arg :input, non_null(:delete_frame_request)
      resolve &NarwhalUiWeb.Resolvers.Frame.delete_frame/3
    end

    @desc "Update an existing frame"
    field :update_frame, :update_frame_response do
      arg :input, non_null(:update_frame_request)
      resolve &NarwhalUiWeb.Resolvers.Frame.update_frame/3
    end

    # Timeline stuff
    @desc "Create a new timeline"
    field :create_timeline, :create_timeline_response do
      arg :input, non_null(:create_timeline_request)
      resolve &NarwhalUiWeb.Resolvers.Timeline.create_timeline/3
    end

    @desc "Delete a timeline"
    field :delete_timeline, :delete_timeline_response do
      arg :input, non_null(:delete_timeline_request)
      resolve &NarwhalUiWeb.Resolvers.Timeline.delete_timeline/3
    end

    @desc "Update an existing timeline"
    field :update_timeline, :update_timeline_response do
      arg :input, non_null(:update_timeline_request)
      resolve &NarwhalUiWeb.Resolvers.Timeline.update_timeline/3
    end
  end
end
