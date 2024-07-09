# SofaProject
The Sofascore Mini App allows users to stay updated with the latest events in football, basketball, and American football. It displays event details, supports navigation to different sections, and offers customizable settings for a personalized experience.

## Key Features
* MVVM Architecture.
* Networking: Retrofit for making API calls.
* Dependency Injection: Hilt for dependency management.
* Asynchronous Programming: Kotlin Coroutines for managing background tasks.
* Pagination for loading large lists.
* Image Loading: Glide for efficient image loading and caching.
* Navigation: Navigation Component with Safe Args for type-safe navigation and passing data between fragments.
* Data Persistence: SharedPreferences for storing user preferences.
* Custom Layouts: Custom central layout for date tab.
* JSON Handling: Custom JSON for country-code mapping and displaying flag images.
* Versioning: TOML catalog for versioning dependencies.

## Screens
_Main List Page_: 
* Displays all events for a specific date, including event status, start date, team names, logos, and scores.
* Different views for events that haven’t started, are underway, or are finished.
* Events are grouped by tournament and ordered by start date.
* Empty state message for days with no events.
* Supported Sports: Football, Basketball, American football

_Event Details_:
* Displays information about the event, including sport, tournament, round details, team names, logos, scores, match status, and incidents.
* If events hasn't started you can navigate to Tournaments page via button or header.

_Tournament Details_:
* Matches Tab: Lists previous and upcoming matches, sorted by start date. -> Pagination
** Event items similar to Main List Page.
** Round items labeled with the name of the round, when oppened displays last active round
** Scroll up or down to load old or new rounds
* Standings Tab: Shows team standings. (Each sport has different metrics)

_Settings_:
* Theme change (Material Theme Light and Dark themes).
* Date format options (DD/MM/YYYY and MM/DD/YYYY).
* About section with project and developer details.

### Screenshots
<div align="center">
    <img src="SofascoreMini/screenshots/Screenshot_empty_main_list.jpg?raw=true" width="140px"</img> 
    <img height="0" width="18px">
    <img src="SofascoreMini/screenshots/Screenshot_main_list_page.jpg?raw=true" width="140px"</img>
    <img height="0" width="18px">
    <img src="SofascoreMini/screenshots/Screenshot_empty_event_details.jpg?raw=true" width="140px"</img> 
    <img height="0" width="18px">
    <img src="SofascoreMini/screenshots/Screenshot_event_details_football.jpg?raw=true" width="140px"</img> 
    <img height="0" width="18px">
    <img src="SofascoreMini/screenshots/Screenshot_event_details_am_football.jpg?raw=true" width="140px"</img> 
    <img height="0" width="18px">
    <img src="SofascoreMini/screenshots/Screenshot_tournament_matches.jpg?raw=true" width="140px"</img> 
    <img height="0" width="18px">
    <img src="/SofascoreMini/screenshots/Screenshot_tournament_standings.jpg?raw=true" width="140px"</img> 
    <img height="0" width="18px">
    <img src="SofascoreMini/screenshots/Screenshot_settings_light.jpg?raw=true" width="140px"</img> 
    <img height="0" width="18px">
    <img src="/SofascoreMini/screenshots/Screenshot_settings_dark.jpg?raw=true" width="140px"</img> 
    <img height="0" width="18px">
    <img src="SofascoreMini/screenshots/Screenshot_leagues_page.jpg?raw=true" width="140px"</img>
    <img height="0" width="18px">
</div>

## API
API documentation: https://academy-backend.sofascore.dev/api/docs.
API is public so no need for API Key.

## License
Copyright 2022 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
