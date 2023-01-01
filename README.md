# Blood-Donation-Application-
Blood Donation Application With Email sending and receiving functions by Temgire Dinesh


Glide Dependencies :

What is Glide in Android?
Glide is an Image Loader Library for Android developed by bumptech and is a library that is recommended by Google.
It has been used in many Google open source projects including Google I/O 2014 official application. 
It provides animated GIF support and handles image loading/caching.


***********************************CODE FOR HOW TO USE GLIDE **************************************************
// For a simple view:
@Override public void onCreate(Bundle savedInstanceState) {
  ...
  ImageView imageView = (ImageView) findViewById(R.id.my_image_view);

  Glide.with(this).load("http://goo.gl/gEgYUd").into(imageView);
}

// For a simple image list:
@Override public View getView(int position, View recycled, ViewGroup container) {
  final ImageView myImageView;
  if (recycled == null) {
    myImageView = (ImageView) inflater.inflate(R.layout.my_image_view, container, false);
  } else {
    myImageView = (ImageView) recycled;
  }

  String url = myUrls.get(position);

  Glide
    .with(myFragment)
    .load(url)
    .centerCrop()
    .placeholder(R.drawable.loading_spinner)
    .into(myImageView);

  return myImageView;
}
******************************************************************************************************

dependencies {
  implementation 'com.github.bumptech.glide:glide:4.14.2'
  annotationProcessor 'com.github.bumptech.glide:compiler:4.14.2'
}
