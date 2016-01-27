package com.example.jonathan.cs499foodlette;

import java.util.List;

public class Business {
	private String id;
	private String name;
	private String image_url;
	private String url;
	private String mobile_url;
	private String phone;
	private String display_phone;
	private int review_count;
	private List<List<String>> categories;
	private double distance;
	private String ratingImgUrl;
	private String rating_img_url_small;
	private String rating_img_url_large;
	private String snippet_text;
	private String snippet_image_url;
	private Location location;
	private String menu_provider;
	private double rating;

	public double getRating(){
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String imageUrl) {
		this.image_url = imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMobile_url() {
		return mobile_url;
	}
	public void setMobile_url(String mobileUrl) {
		this.mobile_url = mobileUrl;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getDisplay_phone() {
		return display_phone;
	}
	public void setDisplay_phonee(String displayPhone) {
		this.display_phone = displayPhone;
	}
	public int getReview_count() {
		return review_count;
	}
	public void setReview_count(int reviewCount) {
		this.review_count = reviewCount;
	}
	public List<List<String>> getCategories() {
		return categories;
	}
	public void setCategories(List<List<String>> categories) {
		this.categories = categories;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public String getRatingImgUrl() {
		return ratingImgUrl;
	}
	public void setRatingImgUrl(String ratingImgUrl) {
		this.ratingImgUrl = ratingImgUrl;
	}
	public String getRating_img_url_small() {
		return rating_img_url_small;
	}
	public void setRating_img_url_small(String ratingImgUrlSmall) {
		this.rating_img_url_small = ratingImgUrlSmall;
	}
	public String getRating_img_url_large(){
		return rating_img_url_large;
	}
	public void setRating_img_url_large(String rating_img_url_large){
		this.rating_img_url_large = rating_img_url_large;
	}
	public String getSnippet_text() {
		return snippet_text;
	}
	public void setSnippet_text(String snippetText) {
		this.snippet_text = snippetText;
	}
	public String getSnippet_image_url() {
		return snippet_image_url;
	}
	public void setSnippet_image_url(String snippetImgUrl) {
		this.snippet_image_url = snippetImgUrl;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getMenu_provider(){
		return menu_provider;
	}
	public void setMenu_provider(String menu){
		menu_provider = menu;
	}
}
