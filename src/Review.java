
public class Review {
	
	private String reviewID;
	
	private String reviewTitle;
	
	private String reviewContent;
	
	private String reviewer;
	
	private String reviewerLoc;
	
	private String reviewDate;
	
	private String visitDate;
	
	private String reviewRating;
	
	private String restaurantName;
	
	private String restaurantAddr;
	
	private int numPhotos;

	
	public String getReviewerLoc() {
		return reviewerLoc;
	}

	public void setReviewerLoc(String reviewerLoc) {
		this.reviewerLoc = reviewerLoc;
	}

	public int getNumPhotos() {
		return numPhotos;
	}

	public void setNumPhotos(int numPhotos) {
		this.numPhotos = numPhotos;
	}

	public String getReviewID() {
		return reviewID;
	}

	public void setReviewID(String reviewID) {
		this.reviewID = reviewID;
	}

	public String getReviewTitle() {
		return reviewTitle;
	}

	public void setReviewTitle(String reviewTitle) {
		this.reviewTitle = reviewTitle;
	}

	public String getReviewContent() {
		return reviewContent;
	}

	public void setReviewContent(String reviewContent) {
		this.reviewContent = reviewContent;
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}

	public String getReviewerCountry() {
		return reviewerLoc;
	}

	public void setReviewerCountry(String reviewerCountry) {
		this.reviewerLoc = reviewerCountry;
	}

	public String getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(String reviewDate) {
		this.reviewDate = reviewDate;
	}

	public String getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}

	public String getReviewRating() {
		return reviewRating;
	}

	public void setReviewRating(String reviewRating) {
		this.reviewRating = reviewRating;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public String getRestaurantAddr() {
		return restaurantAddr;
	}

	public void setRestaurantAddr(String restaurantAddr) {
		this.restaurantAddr = restaurantAddr;
	}
	
	public String extractRating(String className) { 
		// ui_bubble_rating bubble_50
		
		String rating = Character.toString(className.charAt(className.length()-2));
		
		return rating;
	}
}
