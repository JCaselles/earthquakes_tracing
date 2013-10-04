/*
 * RequestHelper
 *
 * Performs the http request querying the IGN (instituto Geográfico
 * Nacional) website for the latest earthquake occurred in Spain.
 *
 * Date: 05/10/2013
 *
 * Author: Arnau Orriols
 */

package com.arnauorriols.apps.terremotosseguimiento;

class RequestHelper {

    private static final DEBUG_TAG = "REQUESTHELPER"

    private static final String BASE_URL = "http://www.ign.es/ign/layoutIn/"
                                           + "sismoListadoTerremotos.do";

    private boolean checkNetwork(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public HashMap<String, String> fetchEarthquakeList(int listDays) throws IOExeption{

        InputStream is = null;
        len = null;

        try {
                URL url = new URL(BASE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is);
                return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                } 
            }

        private Strign readIt (InputStream is){
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String result;
            String line;
            while ((line = br.readline()) != null){
                result += line;
            }
            return result;
        }


    }

