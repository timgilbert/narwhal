defmodule NarwhalUiWeb.Schema.Timeline do
  @moduledoc false

  use Absinthe.Schema.Notation
  alias NarwhalUiWeb.Resolvers.Timeline

  @desc "A timeline"
  object :timeline do
    field :steps, non_null(list_of(non_null(:step))) do
      description "The steps in this timeline"
    end
    field :total,
          non_null(:integer),
          description: """
          The total number of steps in this timeline, including
          repetitions
          """
    field :is_repeat, non_null(:boolean) do
      description "If true, start the timeline over when it gets to the end"
      resolve &Timeline.is_repeat_resolver/3
    end
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
  input_object :timeline_input do
    field :steps, non_null(list_of(non_null(:step_input))),
          description: "List of steps in the timeline"
    field :is_repeat, non_null(:boolean),
          description: "List of steps in the timeline"
  end

  @desc "Input object for a step within a timeline"
  input_object :step_input do
    field :effects, non_null(list_of(non_null(:effect_input))),
          description: "List of effects in this step"
    field :repetitions, non_null(:integer),
          description: "How many times to repeat this step"
    field :pause_ms, non_null(:integer),
          description: "How long to pause after each repetition of this step"
  end

  #  @desc "Input object for one of the effects in a step"
  #  input_object :input_effect do
  #    field :type, non_null(:integer),
  #          description: "Amount of time to pause after this effect finishes"
  #    field :pause_ms, non_null(:integer),
  #          description: "How long to pause after each repetition of this step"
  #  end

  @desc "Input object for a new timeline"
  input_object :create_timeline_request do
    field :name, non_null(:string), description: "Name of the new timeline"
    field :timeline, non_null(:timeline_input),
          description: "The data for the timeline we're saving"
  end

  @desc "Input object for an updated timeline. Note that we currently require the entire object."
  input_object :update_timeline_request do
    field :id, non_null(:string),
          description: "The ID of the timeline we're updating"
    field :name, non_null(:string),
          description: "If non-null, update the timeline's name"
    field :timeline, non_null(:timeline_input),
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
