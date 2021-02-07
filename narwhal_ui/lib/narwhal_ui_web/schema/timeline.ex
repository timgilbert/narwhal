defmodule NarwhalUiWeb.Schema.Timeline do
  @moduledoc false

  use Absinthe.Schema.Notation

  @desc "A timeline"
  object :timeline do
    field :effects, non_null(list_of(non_null(:effect))) do
      description "The effects in this timeline"
      resolve &NarwhalUiWeb.Resolvers.Timeline.effects/3
    end
    field :total, non_null(:integer),
          description: "The total number of steps in this timeline"
    field :is_repeat, non_null(:boolean)
  end

  @desc "A saved timeline with a name and ID"
  object :timeline_metadata do
    field :name, non_null(:string), description: "The name of this timeline"
    field :id, non_null(:string), description: "Unique ID for a saved timeline"
    field :timeline, non_null(:timeline), description: "The timeline data"
  end

  @desc "Response from a create timeline mutation"
  object :create_timeline_response do
    field :timeline, non_null(:timeline_metadata) do
      description "The timeline that was just created"
    end
    field :all_timelines, non_null(list_of(non_null(:timeline_metadata))),
          description: "The new list of all saved timelines"
  end

  @desc "Response from an empty timeline query"
  object :empty_timeline_response do
    field :timeline, non_null(:timeline) do
      description "The empty timeline"
    end
  end

  @desc "Input object for a new timeline"
  input_object :new_timeline do
    field :effects, non_null(list_of(:string)),
          description: "List of steps in the timeline"
  end

  @desc "Input object for a new timeline"
  input_object :create_timeline_request do
    field :name, non_null(:string), description: "Name of the new timeline"
    field :timeline, non_null(:new_timeline),
      description: "The data for the timeline we're saving"
  end

  @desc "Input object for an updated timeline. Note that we currently require the entire object."
  input_object :update_timeline_request do
    field :id, non_null(:string),
          description: "The ID of the timeline we're updating"
    field :name, non_null(:string),
          description: "If non-null, update the timeline's name"
    field :timeline, non_null(:new_timeline),
          description: "If non-null, update the timeline's data"
  end

  @desc "Response from an update timeline mutation"
  object :update_timeline_response do
    field :timeline, non_null(:timeline_metadata),
          description: "The timeline that was just updated"
    field :all_timelines, non_null(list_of(non_null(:timeline_metadata))),
          description: "The new list of all saved timelines"
  end

  @desc "Input object for a timeline deletion"
  input_object :delete_timeline_request do
    field :id, non_null(:string), description: "The ID of the timeline to delete"
  end

  @desc "Response from a delete timeline mutation"
  object :delete_timeline_response do
    field :timeline_id, non_null(:string),
          description: "The ID of the timeline that was just removed"
    field :all_timelines, non_null(list_of(non_null(:timeline_metadata))),
          description: "The new list of all saved timelines"
  end

end
