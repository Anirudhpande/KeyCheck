public class API{
        private String name;
        private String url;
        private String authHeader;

        public API (String name, String url, String authHeader){
            this.name = name;
            this.url = url;
            this.authHeader = authHeader;
        }
        public String getname(){
            return name;
        }
        public String getUrl(){
            return url;
        }
        public String getAuthHeader(){
            return authHeader;
        }
   }
