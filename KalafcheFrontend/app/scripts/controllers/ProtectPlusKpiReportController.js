'use strict';

angular.module('kalafcheFrontendApp')
    .directive('saleReportProtectPlus', function() {
        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'views/partials/sale-report/protect-plus.html',
            controller: ProtectPlusKpiReportController
        }
    });

    function ProtectPlusKpiReportController($scope, SaleService, StoreService) {

        init();

        function init() {
            $scope.currentPage = 1;
            $scope.rowsPerPage = 50;
            $scope.startDate = {};
            $scope.endDate = {};
            $scope.startDateMilliseconds = {};
            $scope.endDateMilliseconds = {};
            $scope.utilityUsageThreshold = 0;
            $scope.selectedUtilityUsageThreshold = 0;
            $scope.selectedTrendMonth = {};
            $scope.selectedTrendStore = null;
            $scope.trendStores = [];
            $scope.trendReportLoading = false;
            $scope.chartReport = null;
            $scope.chartSeries = [];
            $scope.chartMonths = [];
            $scope.chartStores = [];
            $scope.chartAllStoresSelected = false;
            $scope.chartYAxisTicks = [];
            $scope.selectedChartKpi = null;
            $scope.chartKpis = [
                {code: 'activeBase', label: 'Active Base', suffix: ''},
                {code: 'attachRate', label: 'Attach Rate', suffix: '%'},
                {code: 'protectPlusTurnover', label: 'Protect+ Turnover', suffix: '€'},
                {code: 'protectPlusShare', label: 'Protect+ Share', suffix: '%'},
                {code: 'revenuePer100ActiveBase', label: 'Revenue / 100 Active Base', suffix: '€'}
            ];
            $scope.monthNames = ["Януари", "Февруари", "Март", "Април", "Май", "Юни", "Юли", "Август", "Септември", "Октомври", "Ноември", "Декември"];
            $scope.monthShortNames = ["Яну", "Фев", "Мар", "Апр", "Май", "Юни", "Юли", "Авг", "Сеп", "Окт", "Ное", "Дек"];
            $scope.months = [];

            var now = new Date();
            var year = now.getFullYear();
            var month = now.getMonth();

            while ($scope.months.length < 12) {
                if (month == -1) {
                    month = 11;
                    year--;
                    continue;
                }

                var monthObj = {};
                monthObj.fullName = $scope.monthNames[month] + ", " + year;
                monthObj.value = month + "-" + year;
                monthObj.name = $scope.monthNames[month];
                monthObj.year = year;
                $scope.months.push(monthObj);
                month--;
            }

            setDefaultPeriodDates();
            $scope.selectedTrendMonth = $scope.months[0];
            $scope.selectedChartKpi = $scope.chartKpis[2];
            getStores();
        }

        $scope.searchProtectPlusKpiReport = function() {
            var utilityUsageThreshold = resolveUtilityUsageThreshold();
            SaleService.getProtectPlusKpiPeriodReport($scope.startDateMilliseconds, $scope.endDateMilliseconds,
                    utilityUsageThreshold).then(function(response) {
                $scope.selectedUtilityUsageThreshold = utilityUsageThreshold;
                $scope.report = response;
            });
        };

        function setDefaultPeriodDates() {
            $scope.startDate = new Date();
            $scope.startDate.setDate(1);
            $scope.startDate.setHours(0);
            $scope.startDate.setMinutes(0);
            $scope.startDate.setSeconds(0);
            $scope.startDate.setMilliseconds(0);
            $scope.startDateMilliseconds = $scope.startDate.getTime();

            $scope.endDate = new Date();
            $scope.endDate.setHours(23);
            $scope.endDate.setMinutes(59);
            $scope.endDate.setSeconds(59);
            $scope.endDate.setMilliseconds(999);
            $scope.endDateMilliseconds = $scope.endDate.getTime();
        }

        $scope.changeStartDate = function() {
            $scope.startDate.setHours(0);
            $scope.startDate.setMinutes(0);
            $scope.startDate.setSeconds(0);
            $scope.startDate.setMilliseconds(0);
            $scope.startDateMilliseconds = $scope.startDate.getTime();
            $scope.resetCurrentPage();
        };

        $scope.changeEndDate = function() {
            $scope.endDate.setHours(23);
            $scope.endDate.setMinutes(59);
            $scope.endDate.setSeconds(59);
            $scope.endDate.setMilliseconds(999);
            $scope.endDateMilliseconds = $scope.endDate.getTime();
            $scope.resetCurrentPage();
        };

        $scope.searchProtectPlusKpiTrendReport = function() {
            if (!$scope.selectedTrendStore) {
                return;
            }

            $scope.trendReportLoading = true;
            SaleService.getProtectPlusKpiTrendReport($scope.selectedTrendMonth.value, 0, true).then(function(response) {
                $scope.trendReport = response;
                $scope.selectedTrendMonthName = $scope.monthNames[response.selectedMonthMonth] + ", " + response.selectedMonthYear;
                refreshTrendRows();
                clearChart();
            }).finally(function() {
                $scope.trendReportLoading = false;
            });
        };

        $scope.onTrendMonthChanged = function() {
            $scope.trendReport = null;
            $scope.trendRows = [];
            clearChart();
        };

        $scope.generateProtectPlusKpiChart = function() {
            if ($scope.trendReport) {
                $scope.chartReport = $scope.trendReport;
                refreshChartSeries();
                return;
            }

            $scope.trendReportLoading = true;
            SaleService.getProtectPlusKpiTrendReport($scope.selectedTrendMonth.value, 0, true).then(function(response) {
                $scope.trendReport = response;
                $scope.selectedTrendMonthName = $scope.monthNames[response.selectedMonthMonth] + ", " + response.selectedMonthYear;
                refreshTrendRows();
                $scope.chartReport = response;
                refreshChartSeries();
            }).finally(function() {
                $scope.trendReportLoading = false;
            });
        };

        $scope.selectChartKpi = function(kpi) {
            $scope.selectedChartKpi = kpi;
            refreshChartSeries();
        };

        $scope.refreshChartSeries = function() {
            refreshChartSeries();
        };

        $scope.formatChartValue = function(value) {
            if (!$scope.selectedChartKpi) {
                return value;
            }

            return value + $scope.selectedChartKpi.suffix;
        };

        $scope.refreshTrendRows = function() {
            refreshTrendRows();
            clearChart();
        };

        $scope.getRowMonthName = function(row) {
            return getShortMonthLabel(row.month, row.year);
        };

        $scope.getBenchmarkClass = function(row, field) {
            if (!$scope.selectedTrendStore || $scope.selectedTrendStore.id == 0 || !row.companyValues || !isBenchmarkComparable(field)) {
                return '';
            }

            return Number(row[field]) >= Number(row.companyValues[field]) ? 'protect-plus-kpi-above-benchmark' : 'protect-plus-kpi-below-benchmark';
        };

        $scope.formatTrendForecastValue = function(row, valueField, forecastField, suffix) {
            if (!row) {
                return '';
            }

            var value = row[valueField];
            if (value === null || value === undefined) {
                value = 0;
            }

            if (row[forecastField] === null || row[forecastField] === undefined) {
                return value + suffix;
            }

            return value + suffix + ' / ' + row[forecastField] + suffix;
        };

        $scope.toggleAllChartStores = function() {
            var selected = !areAllChartStoresSelected($scope.chartStores);
            angular.forEach($scope.chartStores, function(store) {
                store.selected = selected;
            });
            refreshChartSeries();
        };

        $scope.shouldShowCompanyBenchmarkLine = function() {
            return shouldShowCompanyBenchmarkLine();
        };

        $scope.resetCurrentPage = function() {
            $scope.currentPage = 1;
        };

        function resolveUtilityUsageThreshold() {
            return $scope.utilityUsageThreshold == null || $scope.utilityUsageThreshold === ''
                ? 0 : $scope.utilityUsageThreshold;
        }

        $scope.refreshProtectPlusKpiTrendReport = function() {
            if ($scope.trendReport) {
                $scope.searchProtectPlusKpiTrendReport();
            }
            clearChart();
        };

        function refreshTrendRows() {
            if (!$scope.trendReport || !$scope.selectedTrendStore) {
                $scope.trendRows = [];
                return;
            }

            var companyRowsByMonth = {};
            angular.forEach($scope.trendReport.trendRows || [], function(row) {
                if (row.storeId == 0) {
                    companyRowsByMonth[row.year + '-' + row.month] = row;
                }
            });

            $scope.trendRows = ($scope.trendReport.trendRows || []).filter(function(row) {
                return row.storeId == $scope.selectedTrendStore.id;
            }).map(function(row) {
                row.companyValues = companyRowsByMonth[row.year + '-' + row.month];
                return row;
            });
        }

        function clearChart() {
            $scope.chartReport = null;
            $scope.chartSeries = [];
            $scope.chartStores = [];
            $scope.chartMonths = [];
            $scope.chartYAxisTicks = [];
            $scope.chartAllStoresSelected = false;
        }

        function refreshChartSeries() {
            if (!$scope.chartReport || !$scope.selectedChartKpi) {
                $scope.chartSeries = [];
                $scope.chartStores = [];
                $scope.chartYAxisTicks = [];
                return;
            }

            var rows = $scope.chartReport.trendRows || [];
            var groupedRows = {};
            var monthKeys = [];
            var chartColors = [
                '#9c27b0', '#3f51b5', '#009688', '#ff9800', '#607d8b', '#795548',
                '#e91e63', '#4caf50', '#1565c0', '#ef6c00', '#00838f', '#6a1b9a',
                '#c62828', '#2e7d32', '#ad1457', '#4527a0', '#0277bd', '#558b2f',
                '#f9a825', '#5d4037', '#00897b', '#7b1fa2', '#d84315', '#3949ab',
                '#00acc1', '#8e24aa', '#43a047', '#fb8c00', '#757575', '#c2185b',
                '#1976d2', '#689f38'
            ];
            var selectedStores = {};

            angular.forEach($scope.chartStores, function(store) {
                selectedStores[store.storeId] = store.selected;
            });

            var colorIndex = 0;
            angular.forEach(rows, function(row) {
                var monthKey = row.year + '-' + row.month;
                if (monthKeys.indexOf(monthKey) == -1) {
                    monthKeys.push(monthKey);
                }
                if (!groupedRows[row.storeId]) {
                    var companyRow = row.storeId == 0;
                    groupedRows[row.storeId] = {
                        storeId: row.storeId,
                        storeName: companyRow ? 'Компания' : row.storeName,
                        color: companyRow ? '#222222' : chartColors[colorIndex % chartColors.length],
                        locked: companyRow,
                        lineWidth: companyRow ? 2.4 : 0.8,
                        rowsByMonth: {}
                    };
                    if (!companyRow) {
                        colorIndex++;
                    }
                }
                groupedRows[row.storeId].rowsByMonth[monthKey] = row;
            });

            monthKeys.sort(function(left, right) {
                var leftParts = left.split('-');
                var rightParts = right.split('-');
                return new Date(leftParts[0], leftParts[1], 1) - new Date(rightParts[0], rightParts[1], 1);
            });

            $scope.chartMonths = monthKeys.map(function(monthKey) {
                var parts = monthKey.split('-');
                return {
                    key: monthKey,
                    label: getShortMonthLabel(Number(parts[1]), Number(parts[0]))
                };
            });

            var chartStores = [];
            var chartSeries = [];
            var maxValue = 0;

            angular.forEach(groupedRows, function(storeData) {
                var storeSelected = selectedStores[storeData.storeId];
                var storeSelectionMissing = storeSelected == null;
                if (storeSelected == null) {
                    storeSelected = false;
                }
                if (storeData.locked && shouldShowCompanyBenchmarkLine()) {
                    storeSelected = shouldShowCompanyBenchmarkLine();
                } else {
                    if (storeSelectionMissing && storeData.locked) {
                        storeSelected = true;
                    }
                    chartStores.push({
                        storeId: storeData.storeId,
                        storeName: storeData.storeName,
                        color: storeData.color,
                        locked: storeData.locked,
                        selected: storeSelected
                    });
                }

                if (!storeSelected) {
                    return;
                }

                var points = [];
                angular.forEach($scope.chartMonths, function(month, index) {
                    var row = storeData.rowsByMonth[month.key];
                    var value = row ? Number(row[$scope.selectedChartKpi.code]) || 0 : 0;
                    if (value > maxValue) {
                        maxValue = value;
                    }
                    var x = 80 + index * (700 / Math.max($scope.chartMonths.length - 1, 1));
                    var y = 260;
                    points.push({x: x, y: y, value: value});
                });

                chartSeries.push({
                    storeName: storeData.storeName,
                    color: storeData.color,
                    lineWidth: storeData.lineWidth,
                    points: points
                });
            });

            chartStores.sort(function(left, right) {
                if (left.locked) {
                    return -1;
                }
                if (right.locked) {
                    return 1;
                }
                return left.storeName.localeCompare(right.storeName);
            });

            var chartMaxValue = getChartMaxValue(maxValue);
            angular.forEach(chartSeries, function(series) {
                angular.forEach(series.points, function(point) {
                    point.y = 260 - (chartMaxValue > 0 ? (point.value / chartMaxValue) * 220 : 0);
                });
                series.polylinePoints = series.points.map(function(point) {
                    return point.x + ',' + point.y;
                }).join(' ');
            });

            $scope.chartYAxisTicks = getChartYAxisTicks(chartMaxValue);
            $scope.chartStores = chartStores;
            $scope.chartSeries = chartSeries;
            $scope.chartAllStoresSelected = areAllChartStoresSelected(chartStores);
        }

        function areAllChartStoresSelected(chartStores) {
            if (!chartStores.length) {
                return false;
            }

            var allSelected = true;
            angular.forEach(chartStores, function(store) {
                if (!store.selected) {
                    allSelected = false;
                }
            });

            return allSelected;
        }

        function isBenchmarkComparable(field) {
            return field != 'activeBase' && field != 'protectPlusTurnover';
        }

        function shouldShowCompanyBenchmarkLine() {
            return $scope.selectedChartKpi
                && $scope.selectedChartKpi.code != 'activeBase'
                && $scope.selectedChartKpi.code != 'protectPlusTurnover';
        }

        function getChartMaxValue(maxValue) {
            if (!maxValue) {
                return 0;
            }

            var magnitude = Math.pow(10, Math.floor(Math.log(maxValue) / Math.LN10));
            return Math.ceil(maxValue / magnitude) * magnitude;
        }

        function getChartYAxisTicks(maxValue) {
            var ticks = [];
            for (var i = 0; i <= 4; i++) {
                var value = maxValue * i / 4;
                ticks.push({
                    value: value,
                    label: Math.round(value * 100) / 100,
                    y: 260 - (maxValue > 0 ? (value / maxValue) * 220 : 0)
                });
            }

            return ticks.reverse();
        }

        function getShortMonthLabel(month, year) {
            return $scope.monthShortNames[month] + "'" + String(year).slice(-2);
        }

        function getStores() {
            StoreService.getAllStoresForSaleReport().then(function(response) {
                $scope.trendStores = [{
                    id: 0,
                    city: '',
                    name: 'Всички магазини'
                }].concat(response || []);
                $scope.selectedTrendStore = $scope.trendStores[0];
            });
        }
    };
