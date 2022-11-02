package com.dicoding.storyappsub1.model

import com.google.gson.annotations.SerializedName

data class ResponseAddStory(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
